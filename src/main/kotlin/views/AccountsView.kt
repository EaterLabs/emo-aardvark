package me.eater.emo.aardvark.views

import tornadofx.*

class AccountsView : View() {
    override val root = borderpane {
        id = "accounts-view"

        top = hbox {
            addClass("actionbar")

            labelButton("Add account") {

            }
        }

        center = flowpane {
            label("oh no")
        }
    }
}
