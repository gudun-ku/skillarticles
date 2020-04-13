package ru.skillbranch.skillarticles.viewmodels.base

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.savedstate.SavedStateRegistryOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ViewModelDelegate<T : ViewModel>(
    private val clazz: Class<T>,
    private val owner: SavedStateRegistryOwner?,
    private val arg: Bundle?,
    private val params: Any?
    ) :ReadOnlyProperty<FragmentActivity, T> {

    private lateinit var value: T

    override fun getValue(thisRef: FragmentActivity, property: KProperty<*>): T {
        if (!::value.isInitialized) value = when(arg) {
            null -> ViewModelProviders.of(thisRef).get(clazz)
            else -> ViewModelProviders.of(thisRef, ViewModelFactory(owner!!, arg!!, params!!)).get(clazz)
        }
        return value
    }
}
