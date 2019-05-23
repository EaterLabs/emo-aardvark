package me.eater.emo.aardvark.utils.dto

import com.beust.klaxon.*

data class AdoptOpenJDKReleaseInfo (
    @Json(name = "release_name")
    val releaseName: String,

    @Json(name = "release_link")
    val releaseLink: String,

    val timestamp: String,
    val release: Boolean,
    val binaries: List<AdoptOpenJDKBinary>,

    @Json(name = "download_count")
    val downloadCount: Long
)
