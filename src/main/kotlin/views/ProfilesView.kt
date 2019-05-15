package me.eater.emo.aardvark.views

import tornadofx.*

class ProfilesView : View("My View") {
    override val root = borderpane {
        id = "profiles-view"

        top = hbox {
            addClass("actionbar")

            labelButton("Add profile")
        }
    }
}
