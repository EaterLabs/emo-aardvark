package me.eater.emo.aardvark.views

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import javafx.animation.Timeline
import javafx.scene.control.Label
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.eater.emo.aardvark.controllers.AardvarkController
import me.eater.emo.aardvark.utils.click
import me.eater.emo.aardvark.utils.f
import me.eater.emo.aardvark.utils.labelButton
import me.eater.emo.aardvark.views.profile.CreateProfileFromServer
import me.eater.emo.aardvark.views.profile.ProfileListingView
import tornadofx.*

class ProfilesView : View() {
    private val profilesListingView: ProfileListingView by inject()
    private val createProfileFromServer: CreateProfileFromServer by inject()

    private val aardvarkController: AardvarkController by inject()

    private var refreshIcon: Label by singleAssign()
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
        id = "profiles-view"

        top = hbox {
            addClass("actionbar")

            labelButton {
                f(FontAwesomeIcon.LIST, 14.0)
                label("Show profiles")

                click {
                    if (center != profilesListingView.root) {
                        center.replaceWith(profilesListingView.root)
                    }
                }
            }

            labelButton {
                f(FontAwesomeIcon.PLUS, 14.0)
                label("Add remote profile")

                click {
                    if (center != createProfileFromServer.root) {
                        center.replaceWith(createProfileFromServer.root)
                    }
                }
            }

            labelButton {
                f(FontAwesomeIcon.REFRESH) {
                    refreshIcon = this
                }
                label("Check remote profiles for updates")

                click {
                    updateRemoteProfiles()
                }
            }
        }

        center = profilesListingView.root
    }

    fun updateRemoteProfiles() {
        rotateTimeline.play()

        GlobalScope.launch {
            aardvarkController.updateRemoteProfiles()
            rotateTimeline.stop()
        }
    }

    override fun onDock() {
        if (!ranUpdate) {
            updateRemoteProfiles()
            ranUpdate = false
        }
    }
}
