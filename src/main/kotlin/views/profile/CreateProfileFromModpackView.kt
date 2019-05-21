package me.eater.emo.aardvark.views.profile

import javafx.stage.DirectoryChooser
import me.eater.emo.ModpackCache
import me.eater.emo.aardvark.click
import me.eater.emo.aardvark.controllers.EmoController
import me.eater.emo.aardvark.controllers.InstallerController
import me.eater.emo.aardvark.fragments.ModpackFragment
import me.eater.emo.aardvark.fxprop
import me.eater.emo.aardvark.prop
import me.eater.emo.aardvark.views.MainWindow
import me.eater.emo.aardvark.views.modpack.InstallerView
import me.eater.emo.emo.dto.repository.ModpackVersion
import tornadofx.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant

class CreateProfileFromModpackView : Fragment() {
    private val modpackCache: ModpackCache by param()
    private val version: ModpackVersion by param()

    private val installerController: InstallerController by inject()
    private val emoController: EmoController by inject()

    var location: String by fxprop()
    var name: String by fxprop(modpackCache.modpack.name)

    override val root = vbox {
        addClass("form-container", "wide")

        form {
            fieldset("Create profile") {
                add(
                    find<ModpackFragment>(
                        "modpackCache" to modpackCache,
                        "noButtons" to true,
                        "forceVersion" to version
                    )
                )

                field("Name") {
                    textfield(this@CreateProfileFromModpackView::name.prop())
                }

                field("Location") {
                    textfield {
                        this.textProperty().bindBidirectional(this@CreateProfileFromModpackView::location.prop())
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

            buttonbar {
                button("Cancel") {
                    click {
                        find<MainWindow>().selectModpacks()
                        replaceWith<ProfileListingView>()
                    }
                }

                button("Install") {
                    click {
                        installerController.startInstall(
                            InstallerController.Job(
                                name = name,
                                location = location,
                                modpackVersion = version,
                                modpackCache = modpackCache
                            )
                        )

                        replaceWith<InstallerView>()
                    }
                }
            }
        }
    }

    init {
        val autoDir = emoController.getProfilesDir() + "/${modpackCache.modpack.name.replace(
            Regex("""[^a-z0-9A-Z.-]+"""),
            "_"
        ).toLowerCase()}"
        var path = Paths.get(autoDir)

        if (Files.exists(path) && (!Files.isDirectory(path) || Files.list(path).count() > 0)) {
            path = Paths.get(autoDir + "-${Instant.now().epochSecond}")
        }

        location = path.toString()
    }
}