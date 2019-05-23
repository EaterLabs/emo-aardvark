package me.eater.emo.aardvark.utils.dto

import com.beust.klaxon.Json

data class AdoptOpenJDKVersionData(
    @Json(name = "openjdk_version")
    val openjdkVersion: String,

    val semver: String
)