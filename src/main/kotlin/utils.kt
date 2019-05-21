package me.eater.emo.aardvark

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.property.ObjectProperty
import javafx.beans.property.Property
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.eater.emo.aardvark.styles.Fonts
import tornadofx.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.isAccessible


fun EventTarget.labelButton(hbox: HBox.() -> Unit = {}) = hbox {
    addClass("label-button")
    hbox.invoke(this)
}

fun Node.click(button: MouseButton, block: (MouseEvent) -> Unit) {
    setOnMouseClicked {
        if (it.button != button) return@setOnMouseClicked
        GlobalScope.launch(Dispatchers.JavaFx) {
            block(it)
        }
    }
}

fun Node.click(block: (MouseEvent) -> Unit) = click(MouseButton.PRIMARY, block)

fun EventTarget.f(icon: FontAwesomeIcon, size: Double = 12.0, op: Label.() -> Unit = {}) =
    f(SimpleObjectProperty(icon), size, op)

fun EventTarget.f(icon: ObservableValue<FontAwesomeIcon>, size: Double = 12.0, op: Label.() -> Unit = {}) {
    label(icon.map { it.unicode() }) {
        addClass("font-awesome")
        font = Fonts.fontAwesome

        style {
            fontSize = size.px
            padding = box(horizontal = 5.px)
        }

        op(this)
    }
}

fun <T> box(
    all: T,
    vertical: T = all,
    horizontal: T = all,
    top: T = vertical,
    right: T = horizontal,
    bottom: T = vertical,
    left: T = horizontal
) = CssBox(top, right, bottom, left)

@Suppress("UNCHECKED_CAST")
fun <T : Dimension<Dimension.LinearUnits>> box(
    all: T = 0.px as T,
    vertical: T = all,
    horizontal: T = all,
    top: T = vertical,
    right: T = horizontal,
    bottom: T = vertical,
    left: T = horizontal
) = CssBox(top, right, bottom, left)

fun ObjectProperty<Boolean>.not(): ObservableBooleanValue {
    return map(Boolean::not, ::SimpleBooleanProperty, SimpleBooleanProperty::set)
}

inline fun <T, R, reified RP : ObservableValue<R>> ObservableValue<T>.map(
    noinline op: (T) -> R,
    make: (R) -> RP,
    noinline set: RP.(R) -> Unit
): RP {
    val simple = make(op(this.value))
    this.onChange { set(simple, op(this.value)) }
    return simple
}

fun <T, R> ObservableValue<T>.map(op: (T) -> R): ObservableValue<R> =
    map(op, ::SimpleObjectProperty, SimpleObjectProperty<R>::set)

fun <K, T> fxprop(prop: Property<out T>): ReadWriteProperty<K, T> {
    return object : ReadWriteProperty<K, T>, ChildProperty<T> {
        override fun getProperty(): Property<out T> = prop
        override fun getValue(thisRef: K, property: KProperty<*>): T = prop.value
        override fun setValue(thisRef: K, property: KProperty<*>, value: T) {
            runBlocking {
                GlobalScope.launch(Dispatchers.JavaFx) {
                    prop.value = value
                }
            }
        }
    }
}

fun <K, T> fxprop(default: T? = null): ReadWriteProperty<K, T> = fxprop(SimpleObjectProperty<T>(default))

open class AutoObserver : Observable {
    private val listeners: MutableList<InvalidationListener> = mutableListOf()

    override fun removeListener(listener: InvalidationListener?) {
        listener?.let(listeners::remove)
    }

    override fun addListener(listener: InvalidationListener?) {
        listener?.let(listeners::add)
    }

    @JvmName("notifier")
    private fun notify() {
        this.listeners.forEach {
            it.invalidated(this)
        }
    }

    protected fun <K : AutoObserver, T> observe(default: T): ReadWriteProperty<K, T> =
        fxprop(SimpleObjectProperty(default).apply {
            onChange {
                GlobalScope.launch(Dispatchers.JavaFx) {
                    notify()
                }
            }
        })
}


private fun ensureAccesibility(prop: KProperty<*>) {
    if (!prop.isAccessible) {
        prop.isAccessible = true
    }
}

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

private fun <T> propertyFromDelegate(delegate: Any?): Property<T>? {
    val castedDelegate = delegate as? ChildProperty<*> ?: return null
    @Suppress("UNCHECKED_CAST")
    return castedDelegate.getProperty() as Property<T>
}

interface ChildProperty<T> {
    fun getProperty(): Property<out T>
}