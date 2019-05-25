package me.eater.emo.aardvark.utils.property

import javafx.beans.property.Property
import javafx.beans.value.ObservableValue

@Suppress("UNUSED", "MemberVisibilityCanBePrivate")
class DeepObservableMutableMapProperty<K, V>(var map: MutableMap<K, V> = mutableMapOf()) :
    UnbackedProperty<MutableMap<K, V>>(),
    MutableMap<K, V> {
    val watchedKeys: MutableMap<K, Entry<K, V>> = mutableMapOf()

    override fun internalGet(): MutableMap<K, V> = map

    override fun internalSet(value: MutableMap<K, V>) {
        map = value
    }

    override val size: Int
        get() = map.size

    override fun containsKey(key: K): Boolean = map.containsKey(key)

    override fun containsValue(value: V): Boolean = map.containsValue(value)

    override fun get(key: K): V? = map[key]

    override fun isEmpty(): Boolean = map.isEmpty()

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = map.entries
    override val keys: MutableSet<K>
        get() = map.keys
    override val values: MutableCollection<V>
        get() = map.values

    override fun clear() {
        map.clear()
    }

    fun putOrRemove(key: K, value: V?): V? = synchronized(this) {
        return if (value == null) {
            this.remove(key)
        } else {
            this.put(key, value)
        }
    }

    override fun put(key: K, value: V): V? = synchronized(this) {
        val watcher = this.watchedKeys[key]
        if (watcher != null) {
            val old = watcher.value
            watcher.value = value
            return old
        }

        return map.put(key, value)
    }

    override fun putAll(from: Map<out K, V>) {
        synchronized(this) {
            val old = from.keys
                .filter(this.watchedKeys::containsKey)

            for (key in old) {
                if (this.watchedKeys[key]?.isBound == true) {
                    throw IllegalStateException("Can't putAll given map, $key is bound in our map")
                }
            }

            map.putAll(from.filterKeys { !this.watchedKeys.containsKey(it) })

            for (key in old) {
                this.putOrRemove(key, from[key])
            }
        }
    }

    override fun remove(key: K): V? = synchronized(this) {
        val watcher = this.watchedKeys[key]

        if (watcher != null) {
            val old = watcher.value
            watcher.value = null
            return old
        }

        return map.remove(key)
    }

    fun getObservable(key: K, default: V): ObservableValue<V> = getProperty(key, default)
    fun getProperty(key: K, default: V): Property<V> = getEntry(key).withDefault(default)

    fun getObservable(key: K): ObservableValue<V?> = getProperty(key)
    fun getProperty(key: K): Property<V?> = getEntry(key)

    private fun getEntry(key: K) = watchedKeys.getOrPut(key) {
        Entry(map, key)
    }

    class Entry<K, V>(private val source: MutableMap<K, V>, private val key: K) : UnbackedProperty<V?>() {
        override fun internalGet(): V? = source[key]

        override fun internalSet(value: V?) {
            if (value == null) {
                source.remove(key)
                return
            }

            source[key] = value
        }

        fun notifyListenersFromOutside(oldValue: V?, newValue: V?) {
            notifyListeners(oldValue, newValue)
        }

        @Suppress("UNCHECKED_CAST")
        fun withDefault(default: V): UnbackedProperty<V> {
            val withDefault = EntryWithDefault(source, key, default)
            withDefault.bind(this@Entry as ObservableValue<out V>)
            return withDefault
        }
    }

    class EntryWithDefault<K, V>(private val source: MutableMap<K, V>, private val key: K, private val default: V) :
        UnbackedProperty<V>() {
        override fun internalGet(): V {
            return source[key] ?: default
        }

        override fun internalSet(value: V) {
            if (value == null) {
                source.remove(key)
                return
            }

            source[key] = value
        }

        fun notifyListenersFromOutside(oldValue: V?, newValue: V?) {
            notifyListeners(oldValue ?: default, newValue ?: default)
        }
    }
}