package ru.skillbranch.skillarticles.viewmodels.base

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ViewModelDelegate<T : ViewModel>(private val clazz: Class<T>, private val arg: Any?) :
    ReadOnlyProperty<FragmentActivity, T> {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: FragmentActivity, property: KProperty<*>): T {
         if (clazz.isAssignableFrom(BaseViewModel::class.java)) {
            val viewModel = ViewModelProviders.of(thisRef).get(clazz) as BaseViewModel<IViewModelState>
            viewModel.restoreState(arg as Bundle)
            return viewModel as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }

//        TODO("""
//            Реализуй делегат ViewModelDelegate<T : ViewModel>(private val clazz: Class<T>, private val arg: Any?)
//            :  ReadOnlyProperty<FragmentActivity, T>
//            реализующий получение экземляра BaseViewModel соответствующего типа <T>
//             с аргументами переданными вторым аргументом конструктора.
//            Пример:
//            val viewModel : TestViewModel by provideViewModel("test args")
//        """.trimIndent()) //To change body of created functions use File | Settings | File Templates.

    }
}