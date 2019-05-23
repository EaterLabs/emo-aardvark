package me.eater.emo.aardvark.settings

import com.beust.klaxon.Converter
import com.beust.klaxon.JsonValue
import com.beust.klaxon.Klaxon
import me.eater.emo.EmoEnvironment
import kotlin.reflect.KClass

private fun <T, K : KClass<T>> Klaxon.convert(
    k: K,
    fromJson: Klaxon.(JsonValue) -> T,
    toJson: Klaxon.(T) -> String,
    isUnion: Boolean = false
) =
    this.converter(object : Converter {
        @Suppress("UNCHECKED_CAST")
        override fun toJson(value: Any) = toJson.invoke(this@convert, value as T)

        override fun fromJson(jv: JsonValue) = fromJson.invoke(this@convert, jv) as Any
        override fun canConvert(cls: Class<*>) = cls == k.java || (isUnion && cls.superclass == k.java)
    })

private val deeperKlaxon: Klaxon = Klaxon()
    .convert(
        JavaStyle.AdoptOpenJDK.Implementation::class,
        { JavaStyle.AdoptOpenJDK.Implementation.fromJson(it.string!!) },
        { toJsonString(it.toJson()) }
    )

private val klaxon: Klaxon = Klaxon()
    .convert(
        JavaStyle::class,
        {
            when (it.objString("style")) {
                "system" -> JavaStyle.System(it.objString("location"))
                "adoptopenjdk" -> deeperKlaxon.parseFromJsonObject<JavaStyle.AdoptOpenJDK>(it.obj!!)!!
                "minecraftjdk" -> JavaStyle.MinecraftJDK
                else -> throw IllegalArgumentException()
            }
        },
        { deeperKlaxon.toJsonString(it) }
    )

data class AardvarkSettings(
    var javaStyle: JavaStyle
) {
    fun toJson(): String = klaxon.toJsonString(this)

    companion object {
        fun fromJson(json: String): AardvarkSettings = klaxon.parse(json)!!
        fun default(): AardvarkSettings =
            AardvarkSettings(JavaStyle.default())

    }
}

sealed class JavaStyle {
    abstract val style: String

    class System(val location: String) : JavaStyle() {
        override val style = "system"
    }

    class AdoptOpenJDK(val implementation: Implementation) : JavaStyle() {
        override val style = "adoptopenjdk"

        enum class Implementation {
            HotSpot,
            OpenJ9;

            fun toJson(): String =
                when (this) {
                    HotSpot -> "hotspot"
                    OpenJ9 -> "openj9"
                }

            companion object {
                fun fromJson(value: String): Implementation =
                    when (value) {
                        "hotspot" -> HotSpot
                        "openj9" -> OpenJ9
                        else -> throw IllegalArgumentException()
                    }
            }
        }
    }

    object MinecraftJDK : JavaStyle() {
        override val style = "minecraftjdk"

        fun isAllowed() =
            EmoEnvironment().osName != "linux"

    }

    companion object {
        fun default(): JavaStyle =
            if (MinecraftJDK.isAllowed())
                AdoptOpenJDK(AdoptOpenJDK.Implementation.HotSpot)
            else
                MinecraftJDK

        fun getOptions(): List<String> =
            when (EmoEnvironment().osName) {
                "osx" -> listOf("system", "minecraftjdk")
                "windows" -> listOf("system", "adoptopenjdk", "minecraftjdk")
                "linux" -> listOf("system", "adoptopenjdk")
                else -> listOf("system")
            }
    }
}