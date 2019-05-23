package me.eater.emo.aardvark.views

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import me.eater.emo.aardvark.utils.click
import me.eater.emo.aardvark.controllers.EmoController
import me.eater.emo.aardvark.utils.f
import me.eater.emo.aardvark.utils.labelButton
import me.eater.emo.aardvark.views.account.AccountListView
import me.eater.emo.aardvark.views.account.AccountLoginView
import tornadofx.*

class AccountsView : View() {
    private val emoController: EmoController by inject()
    private val loginView: AccountLoginView by inject()
    private val accountList: AccountListView by inject()

    override val root = borderpane {
        id = "accounts-view"

        top = hbox {
            addClass("actionbar")
            labelButton {
                f(FontAwesomeIcon.PLUS, 14.0)
                label("Add account")

                click {
                    center.replaceWith(loginView.root)
                }
            }
        }

        center = if (emoController.accounts.count() == 0) loginView.root else accountList.root
    }
}
