package me.eater.emo.aardvark.utils

import javafx.event.EventTarget
import javafx.scene.layout.HBox
import tornadofx.addClass
import tornadofx.hbox

fun EventTarget.labelButton(hbox: HBox.() -> Unit = {}) = hbox {
    addClass("label-button")
    hbox.invoke(this)
}