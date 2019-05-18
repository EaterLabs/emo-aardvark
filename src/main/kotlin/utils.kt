package me.eater.emo.aardvark

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import me.eater.emo.aardvark.styles.Fonts
import tornadofx.*

fun EventTarget.labelButton(hbox: HBox.() -> Unit = {}) = hbox {
    addClass("label-button")
    hbox.invoke(this)
}

fun Node.click(button: MouseButton, block: (MouseEvent) -> Unit) {
    setOnMouseClicked {
        if (it.button != button) return@setOnMouseClicked
        block(it)
    }
}

fun Node.click(block: (MouseEvent) -> Unit) = click(MouseButton.PRIMARY, block)

fun EventTarget.f(icon: FontAwesomeIcon, size: Double = 12.0, op: Label.() -> Unit = {}) {
    label(icon.unicode()) {
        addClass("font-awesome")
        font = Fonts.fontAwesome

        style {
            fontSize = size.px
            padding = box(horizontal = 5.px)
        }

        op(this)
    }
}

fun <T> box(
    all: T,
    vertical: T = all,
    horizontal: T = all,
    top: T = vertical,
    right: T = horizontal,
    bottom: T = vertical,
    left: T = horizontal
) = CssBox(top, right, bottom, left)

@Suppress("UNCHECKED_CAST")
fun <T : Dimension<Dimension.LinearUnits>> box(
    all: T = 0.px as T,
    vertical: T = all,
    horizontal: T = all,
    top: T = vertical,
    right: T = horizontal,
    bottom: T = vertical,
    left: T = horizontal
) = CssBox(top, right, bottom, left)