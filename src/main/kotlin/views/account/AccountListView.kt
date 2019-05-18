package me.eater.emo.aardvark.views.account

import me.eater.emo.Account
import me.eater.emo.aardvark.controllers.EmoController
import me.eater.emo.aardvark.fragments.BigAccount
import me.eater.emo.aardvark.views.Listing
import tornadofx.find

class AccountListView : Listing<Account>(find(EmoController::class).accounts) {

    override val noItemsText: String
        get() = "No accounts configured yet"

    override fun render(item: Account) = find<BigAccount>("account" to item).root
}
