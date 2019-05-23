package me.eater.emo.aardvark.utils

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ObservableBooleanValue

fun ObjectProperty<Boolean>.not(): ObservableBooleanValue {
    return map(Boolean::not, ::SimpleBooleanProperty, SimpleBooleanProperty::set)
}