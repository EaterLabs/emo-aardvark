package me.eater.emo.aardvark.views

import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.input.MouseButton
import javafx.scene.layout.VBox
import me.eater.emo.aardvark.styles.MainStyle
import tornadofx.*

class MainWindow : View("emo — Eater's Mod Manager") {
    val accountsView: AccountsView by inject()
    val profilesView: ProfilesView by inject()
    val modpacksView: ModpacksView by inject()

    override val root = borderpane {
        addClass("main-window")
        addStylesheet(MainStyle::class)

        center = hbox { }

        top = hbox {
            label("Eater's Mod Organizer") {
                addClass("main-label")
            }

            labelButton("profiles-tab", "Profiles") {
                setOnMouseClicked {
                    if (it.button != MouseButton.PRIMARY) return@setOnMouseClicked
                    this@borderpane.selectAll<Node>(CssRule.c("main-button-label"))
                        .forEach { node: Node -> node.removeClass("current") }
                    this@labelButton.addClass("current")

                    center.replaceWith(profilesView.root)
                }
            }

            labelButton("modpacks-tab", "Modpacks") {
                setOnMouseClicked {
                    if (it.button != MouseButton.PRIMARY) return@setOnMouseClicked

                    this@borderpane.selectAll<Node>(CssRule.c("main-button-label"))
                        .forEach { node: Node -> node.removeClass("current") }
                    this@labelButton.addClass("current")

                    center.replaceWith(modpacksView.root)
                }
            }

            labelButton("accounts-tab", "Accounts") {

                setOnMouseClicked {
                    if (it.button != MouseButton.PRIMARY) return@setOnMouseClicked

                    this@borderpane.selectAll<Node>(CssRule.c("main-button-label"))
                        .forEach { node: Node -> node.removeClass("current") }

                    this@labelButton.addClass("current")

                    center.replaceWith(accountsView.root)
                }
            }
        }
    }

    fun EventTarget.labelButton(id: String, text: String, block: VBox.() -> Unit = {}) = vbox {
        this.id = id

        addClass("main-button-label")

        label(text)

        block(this)
    }
}
