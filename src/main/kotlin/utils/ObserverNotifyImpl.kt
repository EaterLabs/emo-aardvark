package me.eater.emo.aardvark.utils

import javafx.beans.InvalidationListener
import javafx.beans.Observable

open class ObserverNotifyImpl : Observable {
    private val listeners: MutableList<InvalidationListener> = mutableListOf()

    override fun removeListener(listener: InvalidationListener?) {
        listener?.let(listeners::remove)
    }

    override fun addListener(listener: InvalidationListener?) {
        listener?.let(listeners::add)
    }

    @JvmName("notifier")
    protected fun notify() {
        this.listeners.forEach {
            it.invalidated(this)
        }
    }
}