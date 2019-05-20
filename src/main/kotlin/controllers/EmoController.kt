package me.eater.emo.aardvark.controllers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import me.eater.emo.Account
import me.eater.emo.EmoInstance
import me.eater.emo.emo.RepositoryDefinition
import me.eater.emo.emo.RepositoryType
import tornadofx.Controller
import tornadofx.asObservable

class EmoController : Controller() {
    private val emo: EmoInstance = EmoInstance()
    val accounts by lazy { emo.getAccounts().asObservable() }
    val modpacks by lazy { emo.getModpacks().asObservable() }
    val modpacksList by lazy { modpacks.values.toMutableList().asObservable() }
    val repositories by lazy { getRepositoryCaches().asObservable() }

    suspend fun logIn(username: String, password: String): Account =
        emo.accountLogIn(username, password).also {
            updateAccounts()
        }

    suspend fun logOut(uuid: String) =
        emo.removeAccount(uuid).also {
            updateAccounts()
        }

    private fun getRepositoryCaches() =
        emo.getRepositories().flatMap { emo.getRepository(it.hash)?.let { listOf(it) } ?: listOf() }

    suspend fun logOut(account: Account) = logOut(account.uuid)

    private fun updateAccounts() = fx {
        accounts.setAll(emo.getAccounts())
    }

    suspend fun updateRepositories() {
        emo.updateRepositories()
        updateRepositoryObservers()
        emo.saveModpackCollectionCache()
    }

    fun updateRepositoryObservers() {
        val packs = emo.getModpacks()
        fx {
            modpacks.entries.removeAll(modpacks.entries.filterNot {
                packs.containsKey(it.key)
            })
            modpacks.putAll(emo.getModpacks())
            modpacksList.setAll(modpacks.values)
            repositories.setAll(getRepositoryCaches())
        }
    }


    private fun fx(block: suspend CoroutineScope.() -> Unit) {
        GlobalScope.launch(Dispatchers.JavaFx, block = block)
    }

    fun addRemoteRepository(url: String) {
        emo.useSettings(false) {
            it.repositories.add(RepositoryDefinition(RepositoryType.Remote, url))

            val seen: MutableSet<String> = mutableSetOf()
            val newList = it.repositories.filter { repo ->
                if (seen.contains(repo.hash)) {
                    false
                } else {
                    seen.add(repo.hash)
                    true
                }
            }

            it.repositories.clear()
            it.repositories.addAll(newList)
        }

        GlobalScope.launch {
            updateRepositories()
        }
    }

    fun getRepository(repository: String) =
        emo.getRepository(repository)

    init {
        GlobalScope.launch {
            emo.loadModpackCollectionCache()
        }
    }
}