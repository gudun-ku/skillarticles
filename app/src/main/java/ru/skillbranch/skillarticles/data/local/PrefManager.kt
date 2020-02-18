package ru.skillbranch.skillarticles.data.local


import android.content.Context
import android.content.SharedPreferences

import androidx.preference.PreferenceManager
import ru.skillbranch.skillarticles.data.delegates.PrefDelegate

class PrefManager(context: Context) {

    val preferences: SharedPreferences
            by lazy { PreferenceManager(context).sharedPreferences}

    val storedBoolean by PrefDelegate(false)
    val storedString by PrefDelegate("test")
    val storedInt by PrefDelegate(Int.MAX_VALUE)
    val storedLong by PrefDelegate(Long.MAX_VALUE)
    val storedFloat by PrefDelegate(100f)

    fun clearAll() {
        preferences
            .edit()
            .clear()
            .apply()
    }
}