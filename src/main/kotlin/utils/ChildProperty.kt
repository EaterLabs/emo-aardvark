package me.eater.emo.aardvark.utils

import javafx.beans.property.Property
import javafx.beans.value.ObservableValue

interface ChildProperty<T> : ChildObservableValue<T> {
    fun getProperty(): Property<out T>
    override fun getObservableValue(): ObservableValue<out T> = getProperty()
}