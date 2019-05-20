package me.eater.emo.aardvark.styles

import javafx.scene.paint.Color
import tornadofx.c

@Suppress("UNCHECKED_CAST")
data class Colors(
    val dark: Color = c("#1f2126"),
    val light: Color = c("#d7e7e8"),

    val profiles: Color = c("#1e4e4c"),
    val modpacks: Color = c("#523c1b"),
    val accounts: Color = c("#3b2617"),

    val error: Color = c("#fc5c65"),

    val background: Color = dark,
    val foreground: Color = light
)