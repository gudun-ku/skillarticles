package ru.skillbranch.skillarticles.data.delegates

import ru.skillbranch.skillarticles.data.local.PrefManager
import java.lang.IllegalArgumentException
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PrefDelegate<T>(private val defaultValue: T) : ReadWriteProperty<PrefManager, T?> {


    @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
    override fun getValue(thisRef: PrefManager, property: KProperty<*>): T? {
        val prefs = thisRef.sharedPreferences
        return when(defaultValue) {
            is Boolean -> prefs.getBoolean(property.name, defaultValue as Boolean)
            is String -> prefs.getString(property.name, defaultValue as String)
            is Int -> prefs.getInt(property.name, defaultValue as Int)
            is Long -> prefs.getLong(property.name, defaultValue as Long)
            is Float -> prefs.getFloat(property.name, defaultValue as Float)
            else -> throw IllegalArgumentException("Only primitive types allowed to be read from shared preferences")
        } as T
    }

    override fun setValue(thisRef: PrefManager, property: KProperty<*>, value: T?) {
        val prefsEditor = thisRef.sharedPreferences.edit()
        when (value) {
            is Boolean -> prefsEditor.putBoolean(property.name, value as Boolean)
            is String -> prefsEditor.putString(property.name, value as String)
            is Int -> prefsEditor.putInt(property.name, value as Int)
            is Long -> prefsEditor.putLong(property.name, value as Long)
            is Float -> prefsEditor.putFloat(property.name, value as Float)
            else -> throw IllegalArgumentException("Only primitive types allowed to be save in shared preferences")
        }
        prefsEditor.apply()
    }
}