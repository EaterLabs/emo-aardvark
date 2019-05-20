package me.eater.emo.aardvark.views
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import me.eater.emo.aardvark.f
import me.eater.emo.aardvark.labelButton
import tornadofx.*

class ProfilesView : View() {
    override val root = borderpane {
        id = "profiles-view"

        top = hbox {
            addClass("actionbar")

            labelButton {
                f(FontAwesomeIcon.PLUS, 14.0)
                label("Add profile")
            }
        }
    }
}
