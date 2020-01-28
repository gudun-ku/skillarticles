package ru.skillbranch.skillarticles.ui.delegates

import ru.skillbranch.skillarticles.ui.base.Binding
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class RenderProp<T>(
    var value: T,
    needInit: Boolean = true,
    private val onChange: ((T) -> Unit)? = null
): ReadWriteProperty<Binding, T> {

    init {
        if (needInit) onChange?.invoke(value)
    }

    override fun getValue(thisRef: Binding, property: KProperty<*>): T = value

    override fun setValue(thisRef: Binding, property: KProperty<*>, value: T) {
        if (value == this.value) return
        this.value = value
        onChange?.invoke(this.value)
    }
}