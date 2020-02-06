package ru.skillbranch.skillarticles.viewmodels.base

import android.os.Bundle
import ru.skillbranch.skillarticles.viewmodels.ArticleState

open interface IViewModelState {
    fun save(outState: Bundle)
    fun restore(savedState: Bundle):IViewModelState
}