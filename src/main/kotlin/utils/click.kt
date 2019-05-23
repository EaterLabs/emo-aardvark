package me.eater.emo.aardvark.utils

import javafx.scene.Node
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch

fun Node.click(button: MouseButton, block: (MouseEvent) -> Unit) {
    setOnMouseClicked {
        if (it.button != button) return@setOnMouseClicked
        GlobalScope.launch(Dispatchers.JavaFx) {
            block(it)
        }
    }
}

fun Node.click(block: (MouseEvent) -> Unit) = click(MouseButton.PRIMARY, block)