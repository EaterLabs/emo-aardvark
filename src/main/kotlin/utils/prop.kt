package me.eater.emo.aardvark.utils

import javafx.beans.property.Property
import me.eater.emo.aardvark.utils.ObservableUtils.ensureAccesibility
import me.eater.emo.aardvark.utils.ObservableUtils.propertyFromDelegate
import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1

fun <K, T> K.prop(child: KProperty1<K, T>): Property<T> {
    ensureAccesibility(child)
    return propertyFromDelegate(child.getDelegate(this))
        ?: throw RuntimeException("$child is not backed by a Property")
}

fun <T> KProperty0<T>.prop(): Property<T> = prop(this)
fun <K, T> KProperty1<K, T>.prop(scope: K): Property<out T> = scope.prop(this)

@JvmName("kProp")
fun <T> prop(child: KProperty0<T>): Property<T> {
    ensureAccesibility(child)
    return propertyFromDelegate(child.getDelegate())
        ?: throw RuntimeException("$child is not backed by a Property")
}