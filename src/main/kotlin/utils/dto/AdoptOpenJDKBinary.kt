package me.eater.emo.aardvark.utils.dto

import com.beust.klaxon.Json

data class AdoptOpenJDKBinary (
    val os: String,
    val architecture: String,

    @Json(name = "binary_type")
    val binaryType: String,

    @Json(name = "openjdk_impl")
    val openjdkImpl: String,

    @Json(name = "binary_name")
    val binaryName: String,

    @Json(name = "binary_link")
    val binaryLink: String,

    @Json(name = "binary_size")
    val binarySize: Long,

    @Json(name = "checksum_link")
    val checksumLink: String,

    val version: String,

    @Json(name = "version_data")
    val versionData: AdoptOpenJDKVersionData,

    @Json(name = "heap_size")
    val heapSize: String,

    @Json(name = "download_count")
    val downloadCount: Long,

    @Json(name = "updated_at")
    val updatedAt: String
)