package me.eater.emo.aardvark.fragments

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import javafx.beans.property.ObjectProperty
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import me.eater.emo.ModpackCache
import me.eater.emo.aardvark.click
import me.eater.emo.aardvark.controllers.EmoController
import me.eater.emo.aardvark.f
import me.eater.emo.aardvark.labelButton
import me.eater.emo.aardvark.map
import me.eater.emo.aardvark.views.MainWindow
import me.eater.emo.aardvark.views.ProfilesView
import me.eater.emo.aardvark.views.profile.CreateProfileFromModpackView
import me.eater.emo.emo.dto.repository.ModpackVersion
import net.swiftzer.semver.SemVer
import tornadofx.*

class ModpackFragment : Fragment() {
    private val emoController: EmoController by inject()

    private val modpackCache: ModpackCache by param()
    private val forceVersion: ModpackVersion? by param()
    private val noButtons: Boolean by param(false)

    private val modpack by lazy { modpackCache.modpack }
    private val repository by lazy { emoController.getRepository(modpackCache.repository) }

    private var version: ModpackVersion by property()

    private val versionProperty: ObjectProperty<ModpackVersion>
        get() = getProperty(ModpackFragment::version)

    override val root = gridpane {
        addClass("modpack-fragment")

        hgap = 10.0
        vgap = 5.0

        hbox {
            prefWidth = 100.0
            prefHeight = 100.0

            alignment = Pos.CENTER

            imageview(modpack.logo) {
                fitHeight = 100.0
                fitWidth = 100.0
                isPreserveRatio = true
            }

            gridpaneConstraints {
                columnIndex = 0
                rowIndex = 0
                rowSpan = 5
            }
        }

        label(modpack.name) {
            gridpaneConstraints {
                rowIndex = 0
                columnIndex = 1
            }
        }

        text(modpack.description) {
            addClass("modpack-description")

            gridpaneConstraints {
                rowIndex = 1
                columnIndex = 1
            }
        }


        hbox {
            alignment = Pos.BASELINE_LEFT

            label("Version: ")


            combobox<ModpackVersion> {
                items.setAll(modpack.versions.values.sortedByDescending { SemVer.parse(it.version) })
                bindSelected(versionProperty)
                selectionModel.select(0)

                cellFormat {
                    text = "${it.version} - ${it.minecraft}"
                }

                isDisable = noButtons

                if (forceVersion !== null) {
                    selectionModel.select(forceVersion)
                }
            }


            gridpaneConstraints {
                rowIndex = 4
                columnIndex = 2
            }
        }


        hbox {
            alignment = Pos.BASELINE_LEFT

            label("Minecraft: ")
            label(versionProperty.map(ModpackVersion::minecraft))

            gridpaneConstraints {
                rowIndex = 2
                columnIndex = 2
            }
        }

        hbox {
            alignment = Pos.BASELINE_LEFT

            label("Forge: ")
            label(versionProperty.map { if (it.forge == null) "-" else it.forge })

            gridpaneConstraints {
                rowIndex = 3
                columnIndex = 2
            }
        }

        if (!noButtons) {
            hbox {
                addClass("new-profile-button")

                alignment = Pos.BASELINE_CENTER

                labelButton {
                    alignment = Pos.BASELINE_CENTER

                    hgrow = Priority.ALWAYS
                    label("New profile")
                }

                click {
                    val profilesView = find<ProfilesView>()
                    val mainWindow = find<MainWindow>()
                    mainWindow.selectProfiles()
                    profilesView.root.center.replaceWith(find<CreateProfileFromModpackView>("modpackCache" to modpackCache, "version" to version).root)
                }

                gridpaneConstraints {
                    rowIndex = 0
                    columnIndex = 2
                }
            }
        }

        flowpane {
            addClass("modpack-links")

            hgap = 5.0

            modpack.links.homepage?.let { link ->
                labelButton {
                    f(FontAwesomeIcon.GLOBE)
                    label("Website")

                    click {
                        hostServices.showDocument(link)
                    }

                    tooltip(link)
                }
            }

            modpack.links.donate?.let { link ->
                labelButton {
                    f(FontAwesomeIcon.DOLLAR)
                    label("Donate")

                    click {
                        hostServices.showDocument(link)
                    }

                    tooltip(link)
                }
            }

            gridpaneConstraints {
                rowIndex = 4
                columnIndex = 1
            }
        }

        constraintsForColumn(1).hgrow = Priority.ALWAYS
    }
}