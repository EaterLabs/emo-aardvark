package me.eater.emo.aardvark.utils

import javafx.beans.value.ObservableValue

interface ChildObservableValue<T> {
    fun getObservableValue(): ObservableValue<out T>
}