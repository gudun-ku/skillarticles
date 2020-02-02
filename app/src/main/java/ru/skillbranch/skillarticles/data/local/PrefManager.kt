package ru.skillbranch.skillarticles.data.local


import android.content.Context
import android.content.SharedPreferences

import androidx.preference.PreferenceManager

class PrefManager(context: Context) {

    private val sharedPreferences: SharedPreferences
            by lazy { PreferenceManager.getDefaultSharedPreferences(context) }

}