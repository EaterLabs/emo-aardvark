package me.eater.emo.aardvark.utils

import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <K, T> fxprop(observableValue: ObservableValue<out T>): ReadOnlyProperty<K, T> {
    return object : ReadOnlyProperty<K, T>, ChildObservableValue<T> {
        override fun getObservableValue(): ObservableValue<out T> = observableValue
        override fun getValue(thisRef: K, property: KProperty<*>): T = observableValue.value
    }
}

fun <K, T> fxprop(prop: Property<out T>): ReadWriteProperty<K, T> {
    return object : ReadWriteProperty<K, T>, ChildProperty<T> {
        override fun getProperty(): Property<out T> = prop
        override fun getValue(thisRef: K, property: KProperty<*>): T = prop.value
        override fun setValue(thisRef: K, property: KProperty<*>, value: T) {
            runBlocking {
                GlobalScope.launch(Dispatchers.JavaFx) {
                    prop.value = value
                }
            }
        }
    }
}

fun <K, T> fxprop(default: T? = null): ReadWriteProperty<K, T> = fxprop(SimpleObjectProperty<T>(default))