package me.eater.emo.aardvark.views

import javafx.application.Platform
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.VBox
import tornadofx.*

abstract class Listing<T>(private val items: ObservableList<T>) : View() {
    private lateinit var listingVBox: VBox

    open val noItemsText = "No items"
    open val noItemsView = vbox {
        addClass("listing", "listing-empty")

        hbox {
            label(noItemsText) {
                addClass("listing-empty-label")
            }
        }
    }

    val listingView: VBox
        get() = listingVBox

    open val itemsView =
        scrollpane(true) {
            addClass("listing-scrollpane")

            listingVBox = vbox {
                addClass("listing", "listing-filled")
                bindChildren(items) {
                    render(it)
                }
            }
        }


    abstract fun render(item: T): Node

    private val current: Parent
        get() = if (items.count() > 0) itemsView else noItemsView

    final override var root = current

    override fun onDock() {
        if (!Platform.isFxApplicationThread()) throw RuntimeException(":(")
        if (root !== current) {
            root.replaceWith(current)
            root = current
        }
    }

    fun addClass(vararg classes: String) {
        listingView.addClass(*classes)
        noItemsView.addClass(*classes)
    }

    init {
        items.onChange {
            if (!Platform.isFxApplicationThread()) throw RuntimeException(":(")
            if (root !== current) {
                root.replaceWith(current)
                root = current
            }
        }
    }
}

