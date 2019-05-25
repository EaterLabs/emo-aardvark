package me.eater.emo.aardvark.utils.property

import javafx.beans.binding.Bindings
import javafx.beans.property.Property
import javafx.beans.value.ObservableValue

@Suppress("MemberVisibilityCanBePrivate")
abstract class UnbackedProperty<T> : UnbackReadOnlyProperty<T>(), Property<T> {
    protected open var observable: ObservableValue<out T>? = null

    override fun getValue(): T = internalGet()

    override fun setValue(value: T) {
        if (isBound) throw IllegalStateException("Can't set property as it's bound")
        this.changeListener(this, this.internalGet(), value)
    }

    override fun unbind() {
        synchronized(this) {
            this.unlockedUnbind()
        }
    }

    private fun unlockedUnbind() {
        observable?.let {
            it.removeListener(this::changeListener)
            internalSet(it.value)
        }
    }


    protected fun changeListener(@Suppress("UNUSED_PARAMETER") observable: ObservableValue<out T>?, oldValue: T, newValue: T) {
        internalSet(newValue)
        notifyListeners(oldValue, newValue)
    }

    override fun bind(observable: ObservableValue<out T>) {
        synchronized(this) {
            if (observable != this.observable) {
                unlockedUnbind()
                observable.addListener(this::changeListener)
                this.observable = observable
                if (observable.value != this.internalGet()) {
                    internalSet(observable.value)
                    notifyListeners(this.value, observable.value)
                }
            }
        }
    }

    override fun isBound(): Boolean = observable != null

    override fun bindBidirectional(other: Property<T>?) {
        Bindings.bindBidirectional(this, other)
    }

    override fun unbindBidirectional(other: Property<T>?) {
        Bindings.unbindBidirectional(this, other)
    }

    abstract fun internalGet(): T
    abstract fun internalSet(value: T)
}