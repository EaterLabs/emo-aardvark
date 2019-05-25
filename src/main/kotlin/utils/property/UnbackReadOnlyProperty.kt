package me.eater.emo.aardvark.utils.property

import javafx.beans.InvalidationListener
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.value.ChangeListener

abstract class UnbackReadOnlyProperty<T> : ReadOnlyProperty<T> {
    override fun getBean(): Any? = null
    override fun getName(): String? = null

    private val changeListeners: MutableSet<ChangeListener<in T>> = mutableSetOf()
    private val invalidationListeners: MutableSet<InvalidationListener> = mutableSetOf()

    override fun removeListener(listener: ChangeListener<in T>) {
        changeListeners.remove(listener)
    }

    override fun removeListener(listener: InvalidationListener) {
        invalidationListeners.remove(listener)
    }

    override fun addListener(listener: ChangeListener<in T>) {
        changeListeners.add(listener)
    }

    override fun addListener(listener: InvalidationListener) {
        invalidationListeners.add(listener)
    }

    protected fun notifyListeners(oldValue: T, newValue: T) {
        invalidationListeners.forEach { it.invalidated(this) }
        changeListeners.forEach { it.changed(this, oldValue, newValue) }
    }
}