package me.eater.emo.aardvark.views

import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.Parent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import tornadofx.*

abstract class Listing<T>(private val items: ObservableList<T>) : View("My View") {
    open val noItemsText = "No items"

    open val noItemsView = vbox {
        addClass("listing", "listing-empty")

        hbox {
            label(noItemsText) {
                addClass("listing-empty-label")
            }
        }
    }

    open val itemsView =
        scrollpane(true) {
            addClass("listing-scrollpane")

            vbox {
                addClass("listing", "listing-filled")
                bindChildren(items) {
                    render(it)
                }
            }
        }


    abstract fun render(item: T): Node

    private val current: Parent
        get() = if (items.count() > 0) itemsView else noItemsView

    final override val root = current

    init {
        items.onChange {
            GlobalScope.launch(Dispatchers.JavaFx) {
                root.replaceWith(current)
            }
        }
    }
}

