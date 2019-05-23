package me.eater.emo.aardvark.styles

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory
import javafx.scene.text.Font

object Fonts {
    val minecraftia by lazy {
        Font.loadFont(javaClass.getResource("/fonts/Minecraftia.ttf")!!.openStream(), 10.0)!!
    }
    val fontAwesome by lazy {
        Font.loadFont(javaClass.getResourceAsStream(FontAwesomeIconView.TTF_PATH)!!, 10.0)!!
    }
}