package ru.skillbranch.skillarticles.data.local


import android.content.Context
import android.content.SharedPreferences

import androidx.preference.PreferenceManager

class PrefManager(context: Context) {

    val preferences: SharedPreferences
            by lazy { PreferenceManager.getDefaultSharedPreferences(context) }

    fun clearAll() {
        preferences
            .edit()
            .clear()
            .apply()
    }
}