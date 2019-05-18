package me.eater.emo.aardvark.fragments

import javafx.geometry.Pos
import javafx.scene.text.TextAlignment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.eater.emo.Account
import me.eater.emo.aardvark.click
import me.eater.emo.aardvark.controllers.EmoController
import me.eater.emo.aardvark.labelButton
import tornadofx.*

class BigAccount : Fragment() {
    private val emoController: EmoController by inject()

    private val account: Account by param()

    override val root = anchorpane {
        addClass("big-account")

        vbox {
            anchorpaneConstraints {
                leftAnchor = 0
                topAnchor = 0
                bottomAnchor = 0
            }

            label(account.displayName) {
                addClass("username")

                alignment = Pos.CENTER_LEFT

                addClass("minecraftia")
            }
        }

        vbox {
            anchorpaneConstraints {
                rightAnchor = 0
                topAnchor = 0
                bottomAnchor = 0
            }

            alignment = Pos.CENTER_RIGHT

            addClass("buttons")

            labelButton {
                label("Logout")

                click {
                    GlobalScope.launch {
                        emoController.logOut(account)
                    }
                }
            }
        }
    }
}
