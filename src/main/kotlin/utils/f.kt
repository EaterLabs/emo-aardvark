package me.eater.emo.aardvark.utils

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.control.Label
import me.eater.emo.aardvark.styles.Fonts
import tornadofx.addClass
import tornadofx.label
import tornadofx.px
import tornadofx.style

fun EventTarget.f(icon: FontAwesomeIcon, size: Double = 12.0, op: Label.() -> Unit = {}) =
    f(SimpleObjectProperty(icon), size, op)

fun EventTarget.f(icon: ObservableValue<FontAwesomeIcon>, size: Double = 12.0, op: Label.() -> Unit = {}) {
    label(icon.map { it.unicode() }) {
        addClass("font-awesome")
        font = Fonts.fontAwesome

        style {
            fontSize = size.px
            padding = box(horizontal = 5.px)
        }

        op(this)
    }
}