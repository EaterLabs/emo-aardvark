package me.eater.emo.aardvark.utils

import javafx.beans.property.Property
import javafx.beans.value.ObservableValue
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.isAccessible

object ObservableUtils {
    fun ensureAccesibility(prop: KProperty<*>) {
        if (!prop.isAccessible) {
            prop.isAccessible = true
        }
    }

    fun <T> propertyFromDelegate(delegate: Any?): Property<T>? {
        val castedDelegate = delegate as? ChildProperty<*> ?: return null
        @Suppress("UNCHECKED_CAST")
        return castedDelegate.getProperty() as Property<T>
    }

    fun <T> observablePropertyFromDelegate(delegate: Any?): ObservableValue<T>? {
        val castedDelegate = delegate as? ChildObservableValue<*> ?: return null
        @Suppress("UNCHECKED_CAST")
        return castedDelegate.getObservableValue() as ObservableValue<T>
    }
}