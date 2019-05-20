package me.eater.emo.aardvark.styles

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.scene.text.Font

object Fonts {
    val minecraftia = Font.loadFont(javaClass.getResource("/fonts/Minecraftia.ttf")!!.openStream(), 10.0)!!
    val fontAwesome = Font.loadFont(FontAwesomeIconView::class.java.getResource(FontAwesomeIconView.TTF_PATH)!!.openStream(), 10.0)!!
}