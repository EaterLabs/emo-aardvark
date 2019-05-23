package me.eater.emo.aardvark.views

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import javafx.animation.Timeline
import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import me.eater.emo.aardvark.utils.click
import me.eater.emo.aardvark.controllers.EmoController
import me.eater.emo.aardvark.utils.f
import me.eater.emo.aardvark.utils.labelButton
import me.eater.emo.aardvark.views.modpack.AddRepositoryView
import me.eater.emo.aardvark.views.modpack.ModpackListingView
import me.eater.emo.aardvark.views.modpack.RepositoryListingView
import tornadofx.*

class ModpacksView : View() {
    private val emoController: EmoController by inject()

    private val addRepositoryView: AddRepositoryView by inject()
    private val repositoryListingView: RepositoryListingView by inject()
    private val modpackListingView: ModpackListingView by inject()

    private var refreshIcon: Label by singleAssign()
    private var updateRepositoriesButton: HBox by singleAssign()
    private val rotateTimeline by lazy {
        timeline(false) {
            keyframe(0.seconds) {
                keyvalue(refreshIcon.rotateProperty(), 0)
            }

            keyframe(1.seconds) {
                keyvalue(refreshIcon.rotateProperty(), 359)
            }

            cycleCount = Timeline.INDEFINITE
        }
    }

    private var ranUpdate = false

    override val root = borderpane {
        id = "modpacks-view"

        top = hbox {
            addClass("actionbar")

            labelButton {
                f(FontAwesomeIcon.LIST_ALT, 14.0)
                label("Show modpacks")

                click {
                    if (center != modpackListingView.root) {
                        if (!Platform.isFxApplicationThread()) throw RuntimeException(":(")
                        center.replaceWith(modpackListingView.root)
                    }
                }
            }

            labelButton {
                f(FontAwesomeIcon.LIST, 14.0)
                label("Show repositories")

                click {
                    center.replaceWith(repositoryListingView.root)
                }
            }

            labelButton {
                f(FontAwesomeIcon.PLUS_SQUARE, 14.0)

                label("Add repository")

                click {
                    center.replaceWith(addRepositoryView.root)
                }
            }

            labelButton {
                updateRepositoriesButton = this

                f(FontAwesomeIcon.REFRESH, 14.0) {
                    refreshIcon = this
                }

                label("Refresh repositories")

                click {
                    updateRepositories()
                }
            }
        }

        center = modpackListingView.root
    }

    fun updateRepositories() {
        rotateTimeline.play()
        updateRepositoriesButton.isDisable = true

        GlobalScope.launch {
            emoController.updateRepositories()

            GlobalScope.launch(Dispatchers.JavaFx) {
                rotateTimeline.stop()
                updateRepositoriesButton.isDisable = false
            }
        }
    }

    override fun onDock() {
        if (!ranUpdate) {
            updateRepositories()
            ranUpdate = true
        }
    }
}
