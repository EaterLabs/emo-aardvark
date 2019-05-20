package me.eater.emo.aardvark.views.modpack

import javafx.scene.Node
import me.eater.emo.ModpackCache
import me.eater.emo.aardvark.controllers.EmoController
import me.eater.emo.aardvark.fragments.ModpackFragment
import me.eater.emo.aardvark.views.Listing
import tornadofx.addClass
import tornadofx.find

class ModpackListingView : Listing<ModpackCache>(find(EmoController::class).modpacksList) {
    override fun render(item: ModpackCache): Node = find(ModpackFragment::class, "modpackCache" to item).root

    init {
        listingView.addClass("modpack-listing")
    }
}