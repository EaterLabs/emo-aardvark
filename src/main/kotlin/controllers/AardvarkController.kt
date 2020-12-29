package me.eater.emo.aardvark.controllers

import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.coroutines.awaitString
import com.github.kittinunf.fuel.httpGet
import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import me.eater.emo.EmoEnvironment
import me.eater.emo.ModpackCache
import me.eater.emo.aardvark.AardvarkProfile
import me.eater.emo.aardvark.settings.AardvarkSettings
import me.eater.emo.aardvark.settings.JavaStyle
import me.eater.emo.aardvark.utils.ExtractUtils
import me.eater.emo.aardvark.utils.dto.AdoptOpenJDKReleaseInfo
import me.eater.emo.aardvark.utils.map
import me.eater.emo.aardvark.utils.property.DeepObservableMutableMapProperty
import me.eater.emo.emo.Profile
import me.eater.emo.emo.RepositoryDefinition
import me.eater.emo.emo.RepositoryType
import me.eater.emo.emo.dto.LaunchOptions
import me.eater.emo.emo.dto.repository.Mod
import me.eater.emo.emo.dto.repository.ModpackVersion
import me.eater.emo.emo.dto.repository.Repository
import me.eater.emo.emo.settingsKlaxon
import me.eater.emo.minecraft.JreUtil
import me.eater.emo.minecraft.dto.nbt.Server
import me.eater.emo.utils.parallel
import tornadofx.Controller
import tornadofx.onChange
import java.io.File
import java.time.Instant

class AardvarkController : Controller() {
    private val emoController: EmoController by inject()

    private val settingsFile: File by lazy {
        File("${emoController.getDataDirectory()}/aardvark.json")
    }

    private var _settings: AardvarkSettings? = null
    private var settings: AardvarkSettings
        get() {
            synchronized(this) {
                if (_settings == null)
                    _settings = tryLoadSettings()
            }


            return _settings!!
        }
        set(value) {
            _settings = value
        }

    private val profiles: DeepObservableMutableMapProperty<String, ProfileState> =
        DeepObservableMutableMapProperty(FXCollections.observableHashMap())
    private val remoteProfiles: DeepObservableMutableMapProperty<String, RemoteProfile> =
        DeepObservableMutableMapProperty(mutableMapOf())
    private val profileProcessWatchers: MutableSet<String> = mutableSetOf()

    private val settingsProperty: Property<AardvarkSettings> by lazy {
        SimpleObjectProperty(settings).apply {
            onChange {
                saveSettings()
            }
        }
    }

    val settingsObservable: ObservableValue<AardvarkSettings>
        get() = settingsProperty

    fun <T> useSettings(block: (AardvarkSettings) -> T) {
        val settingsCopy = settings.copy()
        block(settingsCopy)
        settings = settingsCopy
        saveSettings()

        GlobalScope.launch(Dispatchers.JavaFx) {
            settingsProperty.setValue(settingsCopy)
        }
    }

    fun getProfileStateProperty(profile: Profile): ObservableValue<ProfileState> {
        return profiles.getObservable(profile.location, ProfileState.Stopped)
    }

    fun getProfileHasUpdateProperty(profile: AardvarkProfile): ObservableValue<Boolean> {
        return remoteProfiles.getObservable(profile.location).map {
            it != null && (it.modpackVersion.version != profile.modpackVersion.version || it.modpackCache.modpack.name != profile.modpack.name)
        }
    }

    private fun saveSettings() {
        settingsFile.writeText(settings.toJson())
    }

    suspend fun play(profile: Profile) {
        GlobalScope.launch(Dispatchers.JavaFx) {
            profiles[profile.location] = ProfileState.Preparing
        }

        try {
            val launcherJson = File("${profile.location}/.emo/launcher.json")

            val requiresOwnJava = if (launcherJson.exists()) {
                val launchOptions = settingsKlaxon.parse<LaunchOptions>(launcherJson.readText())
                launchOptions?.java != null
            } else settings.javaStyle !is JavaStyle.System

            val defaultJava: String? = if (requiresOwnJava) {
                val style = settings.javaStyle
                when (style) {
                    is JavaStyle.MinecraftJDK -> checkMinecraftJDK()
                    is JavaStyle.AdoptOpenJDK -> checkAdoptOpenJDK(style)
                    else -> null
                }
            } else
                null

            if (profile.location !in profileProcessWatchers) {
                profileProcessWatchers.add(profile.location)
                emoController.getProcessProperty(profile).onChange {
                    profiles[profile.location] = if (it == null || !it.isAlive)
                        ProfileState.Stopped
                    else
                        ProfileState.Running
                }
            }

            emoController.play(profile, defaultJava)
        } catch (e: Throwable) {
            GlobalScope.launch(Dispatchers.JavaFx) {
                profiles[profile.location] = ProfileState.Stopped
            }

            throw e
        }
    }

    private val java: String
        get() = "java${if (EmoEnvironment().osName == "windows") ".exe" else ""}"

    private suspend fun checkAdoptOpenJDK(style: JavaStyle.AdoptOpenJDK): String {
        val aojdkFolder = "${emoController.getDataDirectory()}/jre/adoptopenjdk/${style.implementation.toJson()}"
        val versionFile = File("$aojdkFolder/version")

        if (versionFile.exists() && versionFile.lastModified() > (Instant.now().epochSecond - (7 * 24 * 60 * 60))) {
            val javaExec = "$aojdkFolder/${versionFile.readText().trim()}/bin/$java"
            if (File(javaExec).exists()) {
                return javaExec
            }
        }

        val env = EmoEnvironment()
        val json =
            "https://api.adoptopenjdk.net/v2/info/releases/openjdk8?openjdk_impl=${style.implementation.toJson()}&os=${env.osName}&arch=x${env.osArch}&release=latest&type=jre"
                .httpGet()
                .awaitString()

        val obj: AdoptOpenJDKReleaseInfo = Klaxon().parse(json)!!
        val info = obj.binaries.first()
        val version = "${obj.releaseName.split("_openj9").first()}-jre"
        val aojdkJRE = "$aojdkFolder/$version"
        if (!File("$aojdkJRE/bin/$java").exists()) {
            ExtractUtils.downloadAndExtractArchive(info.binaryLink, aojdkFolder)
        }

        versionFile.writeText(version)
        return "$aojdkJRE/bin/$java"
    }

    private suspend fun checkMinecraftJDK(): String {
        val minecraftJreFolder = "${emoController.getDataDirectory()}/jre/minecraft"
        val versionFile = File("$minecraftJreFolder/version")

        if (versionFile.exists() && versionFile.lastModified() > (Instant.now().epochSecond - (7 * 24 * 60 * 60))) {
            val javaExec = "$minecraftJreFolder/${versionFile.readText().trim()}/bin/$java"
            if (File(javaExec).exists()) {
                return javaExec
            }
        }

        val jre = emoController.getMinecraftJRE()!!

        val minecraftJRE = "$minecraftJreFolder/${jre.version!!}"
        if (!File("$minecraftJRE/bin/$java").exists()) {
            JreUtil.downloadJRE(jre, minecraftJRE)
        }

        versionFile.writeText("${jre.version}")
        return "$minecraftJRE/bin/$java"
    }

    private fun tryLoadSettings(): AardvarkSettings =
        try {
            if (settingsFile.exists()) {
                AardvarkSettings.fromJson(settingsFile.readText()).apply {
                    if (javaStyle is JavaStyle.MinecraftJDK && JavaStyle.MinecraftJDK.isAllowed())
                        javaStyle = JavaStyle.default()
                }
            } else {
                null
            }
        } catch (t: Throwable) {
            t.printStackTrace()

            null
        } ?: AardvarkSettings.default()

    fun stop(profile: Profile) {
        emoController.getProcessProperty(profile).value?.destroy()
    }

    suspend fun getRemoteProfile(handle: String): RemoteProfile? {
        val parts = handle.split('/')
        val first = parts[0]
        val url =
            "https://$first/.well-known/aardvark${if (parts.count() > 1) "/${parts.drop(1).joinToString("/")}" else ""}.json"

        val manifest: ServerDiscoveryManifest = try {
            val json = url
                .httpGet()
                .awaitString()

            Klaxon().parse(json)!!
        } catch (t: Throwable) {
            t.printStackTrace()
            return null
        }

        val repoHash = RepositoryDefinition(RepositoryType.Remote, manifest.repository).hash
        val repositoryCache =
            emoController.getRepository(repoHash)

        val modpack = repositoryCache?.let {
            emoController.modpacks[manifest.modpack]?.modpack
        } ?: run {
            val repoJson = try {
                manifest.repository
                    .httpGet()
                    .awaitString()

            } catch (t: Throwable) {
                return@run null
            }

            val repository: Repository? = Repository.fromJson(repoJson)
            repository?.modpacks?.get(manifest.modpack)
        } ?: return null

        val modpackVersion = modpack.versions.get(manifest.version) ?: return null

        return RemoteProfile(
            handle,
            manifest.name,
            manifest.description,
            ModpackCache(
                repoHash,
                modpack
            ),
            modpackVersion,
            servers = manifest.servers
        )
    }

    suspend fun updateRemoteProfiles() {
        try {
            emoController.updateRepositories()
        } catch (_: Throwable) {
        }

        val remoteProfilesList = emoController.profiles
            .filter { it.isRemote }

        val changedMap = mutableMapOf<String, RemoteProfile>()
        parallel(remoteProfilesList, 5) {
            changedMap[it.location] = getRemoteProfile(it.remote ?: return@parallel) ?: return@parallel
        }

        GlobalScope.launch(Dispatchers.JavaFx) {
            remoteProfiles.putAll(changedMap)
        }
    }

    data class RemoteProfile(
        val remote: String,
        val name: String? = null,
        val description: String?,
        val modpackCache: ModpackCache,
        val modpackVersion: ModpackVersion,
        val servers: List<Server>
    ) {
        fun toJob(
            location: String,
            name: String? = null,
            update: Boolean = false,
            managedMods: List<Mod> = listOf()
        ): InstallerController.Job {
            return InstallerController.Job(
                name = name ?: this.name ?: modpackCache.modpack.name,
                location = location,
                modpackCache = modpackCache,
                modpackVersion = modpackVersion,
                servers = servers,
                remote = remote,
                update = update,
                managedMods = managedMods
            )
        }
    }

    data class ServerDiscoveryManifest(
        val repository: String,
        val modpack: String,
        val version: String,
        val name: String? = null,
        val description: String? = null,
        val servers: List<Server> = listOf()
    )

    enum class ProfileState {
        Stopped,
        Preparing,
        Running;
    }
}
