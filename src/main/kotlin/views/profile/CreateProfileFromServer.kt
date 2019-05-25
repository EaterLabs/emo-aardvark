package me.eater.emo.aardvark.views.profile

import javafx.scene.Node
import javafx.stage.DirectoryChooser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import me.eater.emo.aardvark.controllers.AardvarkController
import me.eater.emo.aardvark.controllers.EmoController
import me.eater.emo.aardvark.controllers.InstallerController
import me.eater.emo.aardvark.fragments.ModpackFragment
import me.eater.emo.aardvark.utils.click
import me.eater.emo.aardvark.utils.fxprop
import me.eater.emo.aardvark.utils.map
import me.eater.emo.aardvark.utils.prop
import tornadofx.*
import java.io.File
import java.time.Instant

class CreateProfileFromServer : View() {
    val emoController: EmoController by inject()
    val aardvarkController: AardvarkController by inject()
    val installerController: InstallerController by inject()

    private val validator = Regex("""[a-z0-9\.-_]+(/[a-z0-9\.-_]+)*""")
    var url: String by fxprop("") {
        tryFetchProfile(it.newValue)
    }

    lateinit var modpackSlot: Node

    var remoteProfile: AardvarkController.RemoteProfile? by fxprop()
    var location: String by fxprop()

    override var root = vbox {
        addClass("form-container", "wide")

        form {
            fieldset("Add profile from server") {
                field("Server identifier") {
                    textfield(::url.prop())
                }


                field("Location") {
                    textfield {
                        this.textProperty().bindBidirectional(::location.prop())
                    }

                    button {
                        label("Select")

                        click {
                            val chooser = DirectoryChooser()
                            var dir = File(location)
                            if (!dir.exists()) {
                                if (!dir.mkdirs()) {
                                    dir = File(emoController.getProfilesDir())
                                    if (!dir.exists()) {
                                        dir.mkdirs()
                                    }
                                }
                            } else {
                                if (!dir.isDirectory) {
                                    dir = dir.parentFile
                                }
                            }

                            chooser.initialDirectory = dir
                            chooser.title = "Select profile install directory"
                            location = chooser.showDialog(primaryStage).absolutePath
                        }
                    }
                }
            }

            modpackSlot = vbox()

            buttonbar {
                button("Cancel") {
                    click {
                        replaceWith<ProfileListingView>()
                    }
                }

                button("Install") {
                    enableWhen(::remoteProfile.map { it != null })

                    click {
                        val remoteProfile: AardvarkController.RemoteProfile = remoteProfile ?: return@click
                        installerController.startInstall(remoteProfile.toJob(location))
                        replaceWith<InstallerView>()
                    }
                }
            }
        }
    }

    fun tryFetchProfile(handle: String) {
        if (!handle.matches(validator)) {
            return
        }

        GlobalScope.launch {
            try {
                delay(300)
                if (handle != ::url.prop().value) {
                    return@launch
                }

                val fetchedProfile = aardvarkController.getRemoteProfile(handle) ?: return@launch

                GlobalScope.launch(Dispatchers.JavaFx) fx@{
                    if (handle != url) {
                        return@fx
                    }

                    remoteProfile = fetchedProfile

                    replaceSlot(
                        find<ModpackFragment>(
                            "modpackCache" to fetchedProfile.modpackCache,
                            "forceVersion" to fetchedProfile.modpackVersion,
                            "noButtons" to true,
                            "name" to (fetchedProfile.name ?: fetchedProfile.modpackCache.modpack.name),
                            "description" to (fetchedProfile.description
                                ?: if (fetchedProfile.name == null) fetchedProfile.modpackCache.modpack.description else "")
                        ).root
                    )

                    val tempLocation = "${emoController.getProfilesDir()}${(fetchedProfile.name
                        ?: handle).replace(Regex("""[^A-Za-z0-9\._-]+"""), "_").trim('_')}"

                    location = if (File(tempLocation).exists())
                        tempLocation + "-${Instant.now().epochSecond}"
                    else tempLocation
                }
            } catch (t: Throwable) {
                if (handle == ::url.prop().value) {
                    replaceSlot(vbox())
                }
            }
        }
    }

    private fun replaceSlot(item: Node) {
        modpackSlot.replaceWith(item)
        modpackSlot = item
    }
}