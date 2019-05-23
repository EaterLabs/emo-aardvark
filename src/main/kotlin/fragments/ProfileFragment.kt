package me.eater.emo.aardvark.fragments

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.eater.emo.aardvark.controllers.AardvarkController
import me.eater.emo.aardvark.controllers.EmoController
import me.eater.emo.aardvark.utils.*
import me.eater.emo.emo.Profile
import me.eater.emo.emo.dto.repository.Modpack
import tornadofx.*
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class ProfileFragment : Fragment() {
    private val profile: Profile by param()
    private val modpack: Modpack
        get() = profile.modpack

    companion object {
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")
            .withLocale(Locale.getDefault(Locale.Category.DISPLAY))
            .withZone(ZoneId.systemDefault())

    }

    private val emoController: EmoController by inject()
    private val aardvarkController: AardvarkController by inject()
    private val profileState: AardvarkController.ProfileState by fxprop(
        aardvarkController.getProfileStateProperty(
            profile
        )
    )

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
            f(::profileState.observe().map {
                when (it) {
                    AardvarkController.ProfileState.Running -> FontAwesomeIcon.STOP
                    AardvarkController.ProfileState.Preparing -> FontAwesomeIcon.WRENCH
                    else -> FontAwesomeIcon.PLAY
                }
            })
            label(::profileState.observe().map {
                when (it) {
                    AardvarkController.ProfileState.Running -> "Stop"
                    AardvarkController.ProfileState.Preparing -> "Preparing..."
                    else -> "Play"
                }
            })

            enableWhen(
                emoController::account.prop().map { it != null }.toBinding()
                    .and(::profileState.observe().map { it != AardvarkController.ProfileState.Preparing }.toBinding())
            )

            click {
                when (profileState) {
                    AardvarkController.ProfileState.Running -> aardvarkController.stop(profile)
                    AardvarkController.ProfileState.Stopped-> GlobalScope.launch {
                        aardvarkController.play(profile)
                    }
                }
            }

            gridpaneConstraints {
                rowIndex = 0
                columnIndex = 3
            }
        }

        hbox {
            label("Last touched: ")
            label(profile.lastTouched.let {
                when (it) {
                    Instant.MIN -> "n/a"
                    else -> dateFormatter.format(it)
                }
            })

            gridpaneConstraints {
                rowIndex = 2
                columnIndex = 3
            }
        }

        hbox {
            label("Created on: ")
            label(profile.createdOn.let {
                when (it) {
                    Instant.MIN -> "n/a"
                    else -> dateFormatter.format(it)
                }
            })

            gridpaneConstraints {
                rowIndex = 3
                columnIndex = 3
            }
        }

        constraintsForColumn(1).hgrow = Priority.ALWAYS
    }
}