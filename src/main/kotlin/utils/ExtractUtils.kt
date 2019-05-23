package me.eater.emo.aardvark.utils

import com.github.kittinunf.fuel.httpDownload
import me.eater.emo.utils.await
import me.eater.emo.utils.io
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarConstants
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream
import java.io.File

object ExtractUtils {
    suspend fun downloadAndExtractArchive(url: String, target: String) {
        val archive = downloadArchive(url)
        extractArchive(archive, target, url.split('.').last().let { if (it in setOf("xz", "gz")) it else null })
        archive.delete()
    }

    suspend fun downloadArchive(url: String): File {
        val temp = File.createTempFile("emo", ".archive")
        url
            .httpDownload()
            .fileDestination { _, _ -> temp }
            .await()

        return temp
    }

    suspend fun extractArchive(file: File, target: String, decompression: String? = null) {
        var fileInputStream = file.inputStream().buffered()

        when (decompression) {
            "xz" -> fileInputStream = XZCompressorInputStream(fileInputStream).buffered()
            "gz" -> fileInputStream = GzipCompressorInputStream(fileInputStream).buffered()
        }

        io {
            val zip = ArchiveStreamFactory().createArchiveInputStream(fileInputStream)
            var entry: ArchiveEntry? = null

            fun nextEntry(): Boolean {
                entry = zip.nextEntry
                return entry != null
            }

            while (nextEntry()) {
                val thisEntry = entry!!
                if (!zip.canReadEntryData(thisEntry))
                    continue

                val entryFile = File("$target/${thisEntry.name}")
                if (thisEntry.isDirectory) {
                    entryFile.mkdirs()
                    continue
                }

                entryFile.parentFile.mkdirs()
                val outputStream = entryFile.outputStream()
                zip.buffered().copyTo(outputStream)
                outputStream.close()

                if (thisEntry is TarArchiveEntry) {
                    entryFile.setExecutable(thisEntry.mode and 73 > 0)
                }
            }

            zip.close()
        }
    }
}