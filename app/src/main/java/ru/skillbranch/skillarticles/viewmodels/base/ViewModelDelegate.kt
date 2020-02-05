package ru.skillbranch.skillarticles.viewmodels.base

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ViewModelDelegate<T : ViewModel>(private val clazz: Class<T>, private val arg: Any?) :
    ReadOnlyProperty<FragmentActivity, T> {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: FragmentActivity, property: KProperty<*>): T {
         if (arg == null) throw IllegalArgumentException("Wrong arguments!")
         if (clazz.isAssignableFrom(BaseViewModel::class.java)) {
            val viewModel = ViewModelProviders.of(thisRef, ViewModelFactory(arg!!)).get(clazz) as BaseViewModel<IViewModelState>
            return viewModel as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}