package me.eater.emo.aardvark.views

import tornadofx.View
import tornadofx.addClass
import tornadofx.borderpane
import tornadofx.hbox

class ModpacksView : View("My View") {
    override val root = borderpane {
        id = "modpacks-view"

        top = hbox {
            addClass("actionbar")


            labelButton("Add modpack")
        }

    }
}
