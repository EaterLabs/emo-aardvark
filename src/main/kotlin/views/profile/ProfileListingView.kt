package me.eater.emo.aardvark.views.profile

import me.eater.emo.aardvark.AardvarkProfile
import me.eater.emo.aardvark.controllers.EmoController
import me.eater.emo.aardvark.fragments.ProfileFragment
import me.eater.emo.aardvark.views.Listing
import me.eater.emo.emo.Profile
import tornadofx.find

class ProfileListingView : Listing<AardvarkProfile>(find(EmoController::class).profiles.sorted { o1, o2 -> -o1.lastTouched.compareTo(o2.lastTouched) }) {
    override val noItemsText: String
        get() = "No profiles created yet"

    override fun render(item: AardvarkProfile) = find(ProfileFragment::class, "profile" to item).root

    init {
        addClass("profile-listing")
    }
}