package me.eater.emo.aardvark.fragments

import javafx.geometry.Pos
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.eater.emo.Account
import me.eater.emo.aardvark.utils.click
import me.eater.emo.aardvark.controllers.EmoController
import me.eater.emo.aardvark.utils.labelButton
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

            label(account.displayName ?: account.username) {
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
