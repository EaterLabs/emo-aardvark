package me.eater.emo.aardvark.utils

import javafx.beans.property.SimpleObjectProperty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import tornadofx.onChange
import kotlin.properties.ReadWriteProperty

open class AutoObserver : ObserverNotifyImpl() {
    protected fun <K : AutoObserver, T> observe(default: T): ReadWriteProperty<K, T> =
        fxprop(SimpleObjectProperty(default).apply {
            onChange {
                GlobalScope.launch(Dispatchers.JavaFx) {
                    notify()
                }
            }
        })
}