package me.eater.emo.aardvark.fragments

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import me.eater.emo.aardvark.AardvarkProfile
import me.eater.emo.aardvark.controllers.AardvarkController
import me.eater.emo.aardvark.controllers.EmoController
import me.eater.emo.aardvark.controllers.InstallerController
import me.eater.emo.aardvark.utils.*
import me.eater.emo.aardvark.views.ProfilesView
import me.eater.emo.aardvark.views.profile.InstallerView
import me.eater.emo.aardvark.views.profile.ProfileSettings
import me.eater.emo.emo.dto.repository.Modpack
import tornadofx.*
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class ProfileFragment : Fragment() {
    private val profile: AardvarkProfile by param()
    private val modpack: Modpack
        get() = profile.modpack

    companion object {
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")
            .withLocale(Locale.getDefault(Locale.Category.DISPLAY))
            .withZone(ZoneId.systemDefault())

    }

    private val emoController: EmoController by inject()
    private val installerController: InstallerController by inject()
    private val aardvarkController: AardvarkController by inject()
    private val profileState: AardvarkController.ProfileState by fxprop(
        aardvarkController.getProfileStateProperty(
            profile.profile
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
                    AardvarkController.ProfileState.Running -> aardvarkController.stop(profile.profile)
                    AardvarkController.ProfileState.Stopped -> GlobalScope.launch {
                        aardvarkController.play(profile.profile)
                    }
                    else -> {
                    }
                }
            }

            gridpaneConstraints {
                rowIndex = 0
                columnIndex = 3
                columnSpan = if (profile.isRemote) 1 else 2
            }
        }

        labelButton {
            f(FontAwesomeIcon.WRENCH)
            label("Settings")

            click {
                find<ProfilesView>().root.center.replaceWith(find<ProfileSettings>("profile" to profile).root)
            }

            gridpaneConstraints {
                rowIndex = 1
                columnIndex = 3
                columnSpan = 2
            }
        }

        if (profile.isRemote) {
            labelButton {
                f(FontAwesomeIcon.UPLOAD)
                val hasUpdate = aardvarkController.getProfileHasUpdateProperty(profile)
                label(hasUpdate.map { if (it) "Update" else "Up-to-date" })
                enableWhen(hasUpdate)

                click {
                    if (hasUpdate.value == true) {
                        GlobalScope.launch {
                            val remoteProfile = aardvarkController.getRemoteProfile(profile.remote!!)!!
                            GlobalScope.launch(Dispatchers.JavaFx) {
                                installerController.startInstall(
                                    remoteProfile.toJob(
                                        profile.location,
                                        profile.name,
                                        true,
                                        profile.modpackVersion.mods
                                    )
                                )

                                find<ProfilesView>().root.center.replaceWith(find<InstallerView>().root)
                            }
                        }
                    }
                }

                gridpaneConstraints {
                    rowIndex = 0
                    columnIndex = 4
                    columnSpan = 1
                }
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
                columnSpan = 2
            }
        }

        hbox {
            isVisible = profile.createdOn != Instant.MIN

            label("Created on: ")
            label(
                if (profile.createdOn != Instant.MIN)
                    dateFormatter.format(profile.createdOn)
                else
                    "broken"
            )

            gridpaneConstraints {
                rowIndex = 3
                columnIndex = 3
                columnSpan = 2
            }
        }

        hbox {
            isVisible = profile.isRemote

            f(FontAwesomeIcon.GLOBE)
            label("Remote profile: ${profile.remote}")

            gridpaneConstraints {
                rowIndex = 3
                columnIndex = 1
            }
        }

        constraintsForColumn(1).hgrow = Priority.ALWAYS
        constraintsForColumn(3).hgrow = Priority.SOMETIMES
        constraintsForColumn(4).hgrow = Priority.SOMETIMES
    }
}