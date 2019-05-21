package me.eater.emo.aardvark.views.modpack

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import javafx.animation.Timeline
import javafx.beans.value.WritableValue
import javafx.scene.control.Label
import kotlinx.coroutines.GlobalScope
import me.eater.emo.aardvark.controllers.InstallerController
import me.eater.emo.aardvark.f
import me.eater.emo.aardvark.fragments.ModpackFragment
import me.eater.emo.aardvark.map
import me.eater.emo.aardvark.prop
import tornadofx.*

class InstallerView : View() {
    private val installerController: InstallerController by inject()
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
            }
        }
    }

    fun updateContent(state: InstallerController.State?) {
        if (state is InstallerController.State.Running) {
            rotateTimeline.play()
        } else {
            rotateTimeline.stop()
        }

        modpackSlot.replaceWith(
            if (state is InstallerController.State.WithJob) {
                find<ModpackFragment>(
                    "noButtons" to true,
                    "forceVersion" to state.job.modpackVersion,
                    "modpackCache" to state.job.modpackCache
                ).root
            } else {
                vbox()
            }
        )
    }

    init {
        installerController::state.prop().onChange {
            updateContent(it)
        }

        updateContent(installerController.state)
    }
}