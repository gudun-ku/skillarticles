package ru.skillbranch.skillarticles.viewmodels.base

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ViewModelDelegate<T : ViewModel>(private val clazz: Class<T>, private val arg: Any?) :
    ReadOnlyProperty<FragmentActivity, T> {

    override fun getValue(thisRef: FragmentActivity, property: KProperty<*>): T {
        TODO("""
            Реализуй делегат ViewModelDelegate<T : ViewModel>(private val clazz: Class<T>, private val arg: Any?) 
            :  ReadOnlyProperty<FragmentActivity, T>  
            реализующий получение экземляра BaseViewModel соответствующего типа <T>
             с аргументами переданными вторым аргументом конструктора.
            Пример:
            val viewModel : TestViewModel by provideViewModel("test args")
        """.trimIndent()) //To change body of created functions use File | Settings | File Templates.
    }
}