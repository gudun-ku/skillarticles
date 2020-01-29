package ru.skillbranch.skillarticles.ui.custom.behaviors

import android.util.Log
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.marginRight
import com.google.android.material.snackbar.Snackbar
import ru.skillbranch.skillarticles.ui.custom.ArticleSubmenu
import ru.skillbranch.skillarticles.ui.custom.Bottombar

class SubmenuBehavior: CoordinatorLayout.Behavior<ArticleSubmenu>() {

    @ViewCompat.NestedScrollType
    private var lastStartedType: Int = 0
    private var snackbarTranslation: Int = 0

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: ArticleSubmenu,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        if (axes != ViewCompat.SCROLL_AXIS_VERTICAL)
            return false
        lastStartedType = type
        return true
    }

    // make view dependent on bottombar
    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: ArticleSubmenu,
        dependency: View
    ): Boolean {
        return dependency is Bottombar
    }

    // will be called if dependend view has changed
    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: ArticleSubmenu,
        dependency: View
    ): Boolean {
        return if(child.isOpen && dependency is Bottombar && dependency.translationY >= 0) {
            animate(child, dependency)
            true
        } else false
    }

    override fun onDependentViewRemoved(
        parent: CoordinatorLayout,
        child: ArticleSubmenu,
        dependency: View
    ) {
        if(dependency is Snackbar.SnackbarLayout ) {
            snackbarTranslation = 0
        }
        super.onDependentViewRemoved(parent, child, dependency)
    }

    private fun animate(child:ArticleSubmenu, dependency: Bottombar){
        val fraction = dependency.translationY/dependency.height
        child.translationX = (child.width + child.marginRight) * fraction
        Log.d("M_SubmenuBehavior", " fraction: $fraction translationX: ${child.translationX}")
    }

}