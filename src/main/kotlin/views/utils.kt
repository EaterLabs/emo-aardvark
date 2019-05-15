package me.eater.emo.aardvark.views

import javafx.event.EventTarget
import javafx.scene.control.*
import javafx.scene.layout.VBox
import tornadofx.*

fun EventTarget.labelButton(text: String, label: Label.() -> Unit = {}, vbox: VBox.() -> Unit = {}) = vbox {
    addClass("label-button")

    label {
        this.text = text
        label.invoke(this)
    }
    vbox.invoke(this)
}