package me.eater.emo.aardvark.views

import javafx.scene.Node
import javafx.scene.layout.VBox
import javafx.scene.shape.Rectangle
import me.eater.emo.aardvark.click
import me.eater.emo.aardvark.controllers.EmoController
import tornadofx.*

class MainWindow : View("emo — Eater's Mod Manager") {
    private val emoController: EmoController by inject()

    private val accountsView: AccountsView by inject()
    private val profilesView: ProfilesView by inject()
    private val modpacksView: ModpacksView by inject()

    override val root = borderpane {
        addClass("main-window", "contrast-light")

        center = if (emoController.accounts.count() == 0) accountsView.root else profilesView.root

        top = vbox {
            addClass("tab-bar")

            clip = Rectangle(this.width, this.height).apply {
                heightProperty().bind(this@vbox.heightProperty())
                widthProperty().bind(this@vbox.widthProperty())
            }


            hbox {
                fun menuButton(id: String, text: String, view: Node, block: VBox.() -> Unit = {}) = vbox {
                    this.id = id

                    addClass("main-button-label")

                    if (view === center) {
                        addClass("current")
                    }

                    label(text)

                    click {
                        this@borderpane.selectAll<Node>(CssRule.c("main-button-label"))
                            .forEach { node: Node -> node.removeClass("current") }

                        addClass("current")
                        center.replaceWith(view)
                    }

                    block(this)
                }

                vbox {
                    addClass("main-label-container")

                    label("Eater's Mod Organizer") {
                        addClass("main-label")
                    }
                }

                menuButton("profiles-tab", "Profiles", profilesView.root)
                menuButton("modpacks-tab", "Modpacks", modpacksView.root)
                menuButton("accounts-tab", "Accounts", accountsView.root)
            }
        }
    }
}