package me.eater.emo.aardvark

import me.eater.emo.emo.Profile
import me.eater.emo.emo.dto.repository.Modpack
import me.eater.emo.emo.dto.repository.ModpackVersion
import java.io.File
import java.time.Instant

class AardvarkProfile(var profile: Profile) {
    val location: String
        get() = profile.location

    val name: String
        get() = profile.name

    val modpack: Modpack
        get() = profile.modpack

    val modpackVersion: ModpackVersion
        get() = profile.modpackVersion

    val createdOn: Instant
        get() = profile.createdOn

    var lastTouched: Instant
        get() = profile.lastTouched
        set(value) {
            profile.lastTouched = value
        }

    val isRemote: Boolean by lazy {
        File("$location/.emo/remote.txt").exists()
    }

    val remote: String? by lazy {
        if (isRemote)
            File("$location/.emo/remote.txt").readText().trim()
        else
            null
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AardvarkProfile

        if (profile != other.profile) return false

        return true
    }

    override fun hashCode(): Int {
        return profile.hashCode()
    }
}