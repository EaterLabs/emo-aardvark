package me.eater.emo.aardvark.utils

import javafx.beans.value.ObservableValue
import me.eater.emo.aardvark.utils.ObservableUtils.ensureAccesibility
import me.eater.emo.aardvark.utils.ObservableUtils.observablePropertyFromDelegate
import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1

fun <K, T> K.observe(child: KProperty1<K, T>): ObservableValue<T> {
    ensureAccesibility(child)
    return observablePropertyFromDelegate(child.getDelegate(this))
        ?: throw RuntimeException("$child is not backed by an ObservableValue")
}

fun <T> KProperty0<T>.observe(): ObservableValue<T> = observe(this)
fun <K, T> KProperty1<K, T>.observe(scope: K): ObservableValue<out T> = scope.observe(this)

@JvmName("kObserve")
fun <T> observe(child: KProperty0<T>): ObservableValue<T> {
    ensureAccesibility(child)
    return observablePropertyFromDelegate(child.getDelegate())
        ?: throw RuntimeException("$child is not backed by an ObservableValue")
}