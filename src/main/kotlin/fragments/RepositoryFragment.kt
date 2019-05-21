package me.eater.emo.aardvark.fragments

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.text.FontPosture
import me.eater.emo.RepositoryCache
import me.eater.emo.aardvark.click
import me.eater.emo.aardvark.controllers.EmoController
import me.eater.emo.aardvark.f
import me.eater.emo.aardvark.labelButton
import me.eater.emo.aardvark.views.ConfirmationView
import me.eater.emo.aardvark.views.MainWindow
import me.eater.emo.aardvark.views.ModpacksView
import tornadofx.*

class RepositoryFragment : Fragment() {
    private val repositoryCache: RepositoryCache by param()
    private val showRemove: Boolean by param(true)


    override val root = gridpane {

        constraintsForColumn(0).prefWidth = 100.0

        vgap = 5.0
        hgap = 10.0

        addClass("repository-fragment")

        hbox {
            alignment = Pos.CENTER

            minWidth = 100.0
            minHeight = 100.0

            imageview(repositoryCache.logo) {
                addClass("repository-icon")
                isPreserveRatio = true
                fitHeight = 100.0
                fitWidth = 100.0
            }

            gridpaneConstraints {
                rowSpan = 4
                rowIndex = 0
                columnIndex = 0
            }
        }

        label(repositoryCache.name) {
            gridpaneConstraints {
                rowIndex = 0
                columnIndex = 1
            }
        }

        text(repositoryCache.description) {
            addClass("repository-description")

            gridpaneConstraints {
                rowIndex = 1
                columnIndex = 1
            }
        }

        if (repositoryCache.status is RepositoryCache.Status.Broken) {
            hbox {
                addClass("repository-broken")

                f(FontAwesomeIcon.WARNING)
                label("Repository is broken")

                tooltip((repositoryCache.status as RepositoryCache.Status.Broken).t.message)

                gridpaneConstraints {
                    rowIndex = 0
                    columnIndex = 2
                }
            }
        }

        flowpane {
            addClass("modpack-links")

            hgap = 5.0

            repositoryCache.links.homepage?.let { link ->
                labelButton {
                    f(FontAwesomeIcon.GLOBE)
                    label("Website")

                    click {
                        hostServices.showDocument(link)
                    }

                    tooltip(link)
                }
            }

            repositoryCache.links.donate?.let { link ->
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

        if (showRemove) {
            labelButton {
                f(FontAwesomeIcon.TRASH)
                label("Remove repository")

                click {
                    openInternalWindow<ConfirmationView>(
                        scope = find<ModpacksView>().scope,
                        owner = find<MainWindow>().root,
                        escapeClosesWindow = false,
                        movable = false,
                        closeButton = false,
                        params = mapOf(
                            "description" to textflow {
                                text("Are you sure you want to delete repository ")
                                text(repositoryCache.name) {
                                    style {
                                        fontStyle = FontPosture.ITALIC
                                    }
                                }
                                text("?")
                            },
                            "callback" to { yes: Boolean ->
                                if (yes) {
                                    find<EmoController>().removeRepository(repositoryCache.definition)
                                }
                            })
                    )
                }

                gridpaneConstraints {
                    rowIndex = 4
                    columnIndex = 2
                }
            }
        }

        constraintsForColumn(1).hgrow = Priority.ALWAYS
    }
}
