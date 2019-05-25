package me.eater.emo.aardvark.utils

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import tornadofx.onChange
import kotlin.reflect.KProperty0

inline fun <T, R, reified RP : ObservableValue<R>> ObservableValue<T>.map(
    noinline op: (T) -> R,
    make: (R) -> RP,
    noinline set: RP.(R) -> Unit
): RP {
    val simple = make(op(this.value))
    this.onChange { set(simple, op(this.value)) }
    return simple
}

fun <T, R> KProperty0<T>.map(op: (T) -> R): ObservableValue<R> =
    this.prop().map(op)

fun <T, R> ObservableValue<T>.map(op: (T) -> R): ObservableValue<R> =
    map(op, ::SimpleObjectProperty, SimpleObjectProperty<R>::set)