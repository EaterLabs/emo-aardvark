package me.eater.emo.aardvark.fragments

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import me.eater.emo.aardvark.*
import me.eater.emo.aardvark.controllers.EmoController
import me.eater.emo.emo.Profile
import me.eater.emo.emo.dto.repository.Modpack
import tornadofx.*

class ProfileFragment : Fragment() {
    private val profile: Profile by param()
    private val modpack: Modpack
        get() = profile.modpack

    val emoController: EmoController by inject()

    var process: Process? by fxprop()

    override val root = gridpane {
        addClass("profile-fragment")

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
                rowSpan = 4
            }
        }

        label(profile.name) {
            gridpaneConstraints {
                rowIndex = 0
                columnIndex = 1
            }
        }

        label(profile.location) {
            addClass("profile-location")

            gridpaneConstraints {
                rowIndex = 1
                columnIndex = 1
            }
        }

        labelButton {
            f(::process.prop().map {
                when {
                    it != null && it.isAlive -> FontAwesomeIcon.STOP
                    else -> FontAwesomeIcon.PLAY
                }
            })
            label(::process.prop().map {
                when {
                    it != null && it.isAlive -> "Stop"
                    else -> "Play"
                }
            })

            enableWhen(emoController::account.prop().map { it != null })

            click {
                if (process != null && process?.isAlive == true) {
                    process?.destroy()
                } else {
                    process = emoController.play(profile).apply {
                        onExit().thenAccept {
                            process = null
                        }
                    }
                }
            }

            gridpaneConstraints {
                rowIndex = 0
                columnIndex = 3
            }
        }

        constraintsForColumn(1).hgrow = Priority.ALWAYS
    }
}