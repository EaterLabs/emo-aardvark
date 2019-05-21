package me.eater.emo.aardvark.views.modpack

import javafx.scene.Node
import me.eater.emo.RepositoryCache
import me.eater.emo.aardvark.controllers.EmoController
import me.eater.emo.aardvark.fragments.RepositoryFragment
import me.eater.emo.aardvark.views.Listing
import tornadofx.addClass
import tornadofx.find

class RepositoryListingView : Listing<RepositoryCache>(find(EmoController::class).repositories) {
    override val noItemsText: String
        get() = "No repositories configured"

    override fun render(item: RepositoryCache): Node {
        return find(RepositoryFragment::class, "repositoryCache" to item).root
    }

    init {
        addClass("repository-listing")
    }
}