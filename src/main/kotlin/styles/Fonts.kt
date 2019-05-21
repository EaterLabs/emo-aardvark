package me.eater.emo.aardvark.styles

import javafx.scene.text.Font

object Fonts {
    val minecraftia by lazy {
        Font.loadFont(javaClass.getResource("/fonts/Minecraftia.ttf")!!.openStream(), 10.0)!!
    }
    val fontAwesome by lazy {
        Font.loadFont(javaClass.getResource("/fonts/fontawesome.ttf")!!.openStream(), 10.0)!!
    }
}