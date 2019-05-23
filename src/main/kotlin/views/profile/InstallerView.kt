package me.eater.emo.aardvark.views.profile

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import javafx.animation.Timeline
import javafx.beans.value.WritableValue
import javafx.scene.control.Label
import me.eater.emo.aardvark.controllers.EmoController
import me.eater.emo.aardvark.controllers.InstallerController
import me.eater.emo.aardvark.fragments.ModpackFragment
import me.eater.emo.aardvark.utils.*
import tornadofx.*

class InstallerView : View() {
    private val installerController: InstallerController by inject()
    private val emoController: EmoController by inject()
    private var modpackSlot = vbox()

    private val rotateProperty = object : WritableValue<Double> {
        fun getSpinner() = root.selectAll<Label>(CssRule.c("installer-spinner")).firstOrNull()

        override fun setValue(value: Double?) {
            getSpinner()?.rotate = value ?: 0.0
        }

        override fun getValue(): Double = getSpinner()?.rotate ?: 0.0
    }

    private val rotateTimeline by lazy {
        timeline(false) {
            keyframe(0.seconds) {
                keyvalue(rotateProperty, 0.0)
            }

            keyframe(1.seconds) {
                keyvalue(rotateProperty, 359.9)
            }

            cycleCount = Timeline.INDEFINITE
        }
    }

    override val root = vbox {
        addClass("installer-container")

        vbox {
            addClass("installer-view")

            label(installerController::state.prop().map {
                when (it) {
                    is InstallerController.State.WithJob -> "Installer for ${it.job.name}"
                    else -> "Installer"
                }
            }) {
                addClass("installer-title")
            }

            vbox {
                add(modpackSlot)
            }

            scrollpane(true) {
                vbox {
                    vbox {
                        addClass("installer-steps")

                        bindChildren(installerController.tasks) {
                            hbox {
                                val stateProp = it::state.prop()

                                f(stateProp.map {
                                    when (it) {
                                        is InstallerController.Task.TaskState.Running -> FontAwesomeIcon.SPINNER
                                        is InstallerController.Task.TaskState.Error -> FontAwesomeIcon.EXCLAMATION_CIRCLE
                                        else -> FontAwesomeIcon.CHECK
                                    }
                                }) {
                                    toggleClass(
                                        CssRule.c("installer-spinner"),
                                        stateProp.map { it is InstallerController.Task.TaskState.Running }
                                    )

                                    stateProp.onChange {
                                        // Reset rotation 0 when set to done
                                        rotate = 0.0
                                    }
                                }

                                label(it.description)
                            }
                        }
                    }

                    vbox {
                        addClass("error", "installer-error")

                        val shouldBePresent = installerController::state.prop()
                            .map(InstallerController.State::hasFailed);

                        visibleWhen(shouldBePresent)
                        managedWhen(shouldBePresent)

                        label(installerController::state.prop().map {
                            "Installation failed: ${(it as? InstallerController.State.Error)?.t?.message ?: "No error"}"
                        }) {
                            addClass("installer-error-title")
                        }

                        textarea {
                            addClass("installer-error-stacktrace")

                            textProperty().bind(installerController::state.prop().map {
                                (it as? InstallerController.State.Error)?.t?.getStrackTrace()
                                    ?: "No stacktrace available"
                            })

                            isEditable = false
                        }
                    }

                    vbox {
                        addClass("installer-buttons-container")

                        visibleWhen(
                            installerController::state.prop()
                                .map { it !is InstallerController.State.Running }
                        )

                        hbox {
                            addClass("installer-buttons")

                            labelButton {
                                label("Close") {
                                    click {
                                        replaceWith<ProfileListingView>()
                                    }
                                }
                            }

                            labelButton {
                                visibleWhen(
                                    installerController::state.prop()
                                        .map { it is InstallerController.State.Done }
                                )

                                label("Play")

                                click {
                                    val state = installerController.state

                                    if (state is InstallerController.State.Done) {
                                        emoController.play(state.profile)
                                        replaceWith<ProfileListingView>()
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    fun updateContent(state: InstallerController.State?) {
        if (state is InstallerController.State.Running) {
            rotateTimeline.play()
        } else {
            rotateTimeline.stop()
        }

        val newSlot = if (state is InstallerController.State.WithJob) {
            find<ModpackFragment>(
                "noButtons" to true,
                "forceVersion" to state.job.modpackVersion,
                "modpackCache" to state.job.modpackCache
            ).root
        } else {
            vbox()
        }

        modpackSlot.replaceChildren(newSlot)
    }

    init {
        installerController::state.prop().onChange {
            updateContent(it)
        }

        updateContent(installerController.state)
    }
}