package me.eater.emo.aardvark.views

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import me.eater.emo.aardvark.f
import me.eater.emo.aardvark.labelButton
import me.eater.emo.aardvark.views.profile.ProfileListingView
import tornadofx.*

class ProfilesView : View() {
    private val profilesListingView: ProfileListingView by inject()

    override val root = borderpane {
        id = "profiles-view"

        top = hbox {
            addClass("actionbar")

            labelButton {
                f(FontAwesomeIcon.PLUS, 14.0)
                label("Add profile")
            }
        }

        center = profilesListingView.root
    }
}
