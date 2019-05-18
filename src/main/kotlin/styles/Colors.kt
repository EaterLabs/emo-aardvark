package me.eater.emo.aardvark.styles

import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import tornadofx.MultiValue
import tornadofx.c
import tornadofx.multi

@Suppress("UNCHECKED_CAST")
data class Colors(
    val dark: Color = c("rgba(56, 60, 68, 1)"),
    val light: Color = c("rgba(215, 231, 232, 1)"),

    val profiles: Color = c("rgb(54, 143, 139)"),
    val modpacks: Color = c("#966d30"),
    val accounts: Color = c("#6b4429"),

    val error: Color = c("#fc5c65"),

    val background: Color = dark,
    val foreground: Color = light
)