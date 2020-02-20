package ru.skillbranch.skillarticles.ui.delegates

import android.content.Context
import androidx.annotation.AttrRes
import ru.skillbranch.skillarticles.extensions.attrValue
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class AttrValue(@AttrRes private val res: Int) : ReadOnlyProperty<Context, Int> {

    private var value: Int? = null
    override fun getValue(thisRef: Context, property: KProperty<*>): Int {
        if(value == null) {
            value = thisRef.attrValue(res)
        }
        return value!!
    }
}