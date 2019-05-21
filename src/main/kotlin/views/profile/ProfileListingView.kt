package me.eater.emo.aardvark.views.profile

import me.eater.emo.aardvark.controllers.EmoController
import me.eater.emo.aardvark.fragments.ProfileFragment
import me.eater.emo.aardvark.views.Listing
import me.eater.emo.emo.Profile
import tornadofx.find

class ProfileListingView : Listing<Profile>(find(EmoController::class).profiles) {
    override val noItemsText: String
        get() = "No profiles created yet"

    override fun render(item: Profile) = find(ProfileFragment::class, "profile" to item).root

    init {
        addClass("profile-listing")
    }
}