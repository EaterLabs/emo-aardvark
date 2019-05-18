package me.eater.emo.aardvark.controllers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import me.eater.emo.Account
import me.eater.emo.EmoInstance
import tornadofx.Controller
import tornadofx.asObservable

class EmoController : Controller() {
    private val emo: EmoInstance = EmoInstance()
    val accounts by lazy { emo.getAccounts().asObservable() }
    val modpacks by lazy { emo.getModpacks().asObservable() }

    suspend fun logIn(username: String, password: String): Account =
        emo.accountLogIn(username, password).also {
            updateAccounts()
        }

    suspend fun logOut(uuid: String) =
        emo.removeAccount(uuid).also {
            updateAccounts()
        }

    suspend fun logOut(account: Account) = logOut(account.uuid)

    private fun updateAccounts() = fx {
        accounts.setAll(emo.getAccounts())
    }

    suspend fun updateRepositories() {
        emo.updateRepositories()
        val packs = emo.getModpacks()
        modpacks.entries.removeAll(modpacks.entries.filterNot {
            packs.containsKey(it.key)
        })
        modpacks.putAll(emo.getModpacks())
    }

    private fun fx(block: suspend CoroutineScope.() -> Unit) {
        GlobalScope.launch(Dispatchers.JavaFx, block = block)
    }
}