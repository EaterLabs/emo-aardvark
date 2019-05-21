package me.eater.emo.aardvark.controllers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import me.eater.emo.*
import me.eater.emo.Target
import me.eater.emo.aardvark.fxprop
import me.eater.emo.aardvark.prop
import me.eater.emo.emo.Profile
import me.eater.emo.emo.RepositoryDefinition
import me.eater.emo.emo.RepositoryType
import me.eater.emo.utils.ProcessStartedEvent
import tornadofx.Controller
import tornadofx.asObservable
import tornadofx.onChange
import java.nio.file.Paths

class EmoController : Controller() {
    private val emo: EmoInstance = EmoInstance()
    val accounts by lazy { mutableListOf(*emo.getAccounts().toTypedArray()).asObservable() }
    val modpacks by lazy { mutableMapOf(*emo.getModpacks().entries.map { it.key to it.value }.toTypedArray()).asObservable() }
    val modpacksList by lazy { modpacks.values.toMutableList().asObservable() }
    val repositories by lazy { getRepositoryCaches().asObservable() }
    val profiles by lazy { mutableListOf(*emo.getProfiles().toTypedArray()).asObservable() }
    var account: Account? by fxprop()

    suspend fun logIn(username: String, password: String): Account =
        emo.accountLogIn(username, password).also {
            updateAccounts()
        }

    suspend fun logOut(uuid: String) =
        emo.removeAccount(uuid).also {
            updateAccounts()
        }

    private fun getRepositoryCaches() =
        emo.getRepositories().flatMap { emo.getRepository(it.hash)?.let { listOf(it) } ?: listOf() }

    suspend fun logOut(account: Account) = logOut(account.uuid)

    private fun updateAccounts() = fx {
        accounts.setAll(emo.getAccounts())
    }

    fun updateProfiles() = fx {
        profiles.setAll(emo.getProfiles())
    }

    suspend fun updateRepositories() {
        try {
            emo.updateRepositories()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        updateRepositoryObservers()
        emo.saveModpackCollectionCache()
    }

    fun updateRepositoryObservers() {
        val packs = emo.getModpacks()
        fx {
            modpacks.entries.removeAll(modpacks.entries.filterNot {
                packs.containsKey(it.key)
            })
            modpacks.putAll(emo.getModpacks())
            modpacksList.setAll(modpacks.values)
            repositories.setAll(getRepositoryCaches())
        }
    }


    private fun fx(block: suspend CoroutineScope.() -> Unit) {
        GlobalScope.launch(Dispatchers.JavaFx, block = block)
    }

    fun addRemoteRepository(url: String) {
        emo.useSettings(false) {
            it.repositories.add(RepositoryDefinition(RepositoryType.Remote, url))

            val seen: MutableSet<String> = mutableSetOf()
            val newList = it.repositories.filter { repo ->
                if (seen.contains(repo.hash)) {
                    false
                } else {
                    seen.add(repo.hash)
                    true
                }
            }

            it.repositories.clear()
            it.repositories.addAll(newList)
        }

        GlobalScope.launch {
            updateRepositories()
        }
    }

    fun getRepository(repository: String) =
        emo.getRepository(repository)

    fun removeRepository(definition: RepositoryDefinition) {
        emo.removeRepository(definition)
        GlobalScope.launch {
            updateRepositories()
        }
    }

    fun getProfilesDir(): String =
        emo.getDataDir() + "/profiles/"

    suspend fun startInstall(emoContext: EmoContext, stateStart: suspend (ProcessStartedEvent<EmoContext>) -> Unit) =
        emo.runInstall(emoContext, stateStart)

    fun getEmoContext(job: InstallerController.Job): EmoContext = with(job) {
        EmoContext(
            installLocation = Paths.get(location),
            forgeVersion = VersionSelector.fromStringOrNull(modpackVersion.forge),
            minecraftVersion = VersionSelector(modpackVersion.minecraft),
            name = name,
            modpackVersion = modpackVersion,
            modpack = modpackCache.modpack.withoutVersions(),
            target = Target.Client,
            mods = modpackVersion.mods,
            instance = emo
        )
    }

    fun play(profile: Profile): Process {
        return emo.getMinecraftExecutor(
            profile,
            account ?: throw RuntimeException("Please select an account before start a profile")
        )
            .execute()
    }


    init {
        GlobalScope.launch {
            emo.loadModpackCollectionCache()

            val uuid = emo.useSettings(true) {
                it.selectedAccount
            }

            ::account.prop().onChange { acc ->
                if (acc == null) return@onChange

                emo.useSettings {
                    it.selectAccount(acc.uuid)
                }
            }

            account = emo.getAccounts().find {
                it.uuid == uuid
            }
        }
    }
}