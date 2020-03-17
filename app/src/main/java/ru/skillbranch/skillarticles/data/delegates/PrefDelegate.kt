package ru.skillbranch.skillarticles.data.delegates

import ru.skillbranch.skillarticles.data.local.PrefManager
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PrefDelegate<T>(private val defaultValue: T) {
    private var storedValue: T? = null

    operator fun provideDelegate(
        thisRef: PrefManager,
        property: KProperty<*>
    ): ReadWriteProperty<PrefManager, T?> {
        val key = property.name
        return object: ReadWriteProperty<PrefManager, T?> {
            override fun getValue(thisRef: PrefManager, property: KProperty<*>): T? {
                if (storedValue == null) {
                    val prefs = thisRef.preferences
                    @Suppress("UNCHECKED_CAST")
                    storedValue = when (defaultValue) {
                        is Boolean -> prefs.getBoolean(property.name, defaultValue as Boolean) as T
                        is String -> prefs.getString(property.name, defaultValue as String) as T
                        is Int -> prefs.getInt(property.name, defaultValue as Int) as T
                        is Long -> prefs.getLong(property.name, defaultValue as Long) as T
                        is Float -> prefs.getFloat(property.name, defaultValue as Float) as T
                        else -> error("Only primitive types allowed to be read from shared preferences")
                    }
                }
                return storedValue
            }

            override fun setValue(thisRef: PrefManager, property: KProperty<*>, value: T?) {
                with (thisRef.preferences.edit()) {
                    when (value) {
                        is Boolean -> putBoolean(property.name, value as Boolean)
                        is String -> putString(property.name, value as String)
                        is Int -> putInt(property.name, value as Int)
                        is Long -> putLong(property.name, value as Long)
                        is Float -> putFloat(property.name, value as Float)
                        else -> error("Only primitive types allowed to be save in shared preferences")
                    }
                    apply()
                }
                storedValue = value
            }
        }
    }

}

