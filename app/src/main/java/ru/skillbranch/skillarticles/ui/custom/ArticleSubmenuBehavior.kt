package ru.skillbranch.skillarticles.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import com.google.android.material.snackbar.Snackbar


class ArticleSubmenuBehavior<V: View>(context: Context, attributeSet: AttributeSet) :
    CoordinatorLayout.Behavior<View>(context,attributeSet) {

    @ViewCompat.NestedScrollType
    private var lastStartedType: Int = 0
    private var snackbarTranslation: Int = 0

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
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

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        //child.translationY = max(0f, min(child.height.toFloat(), child.translationY + dy))
    }

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        return dependency is Bottombar || dependency is Snackbar.SnackbarLayout
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        if(dependency is Bottombar ) {
            if (dependency.translationY > 0)
                child.translationY = 0f + child.height + dependency.marginTop +  dependency.translationY + snackbarTranslation
            else
                child.translationY = 0f
            return true
        }

        if(dependency is Snackbar.SnackbarLayout ) {
            snackbarTranslation = dependency.marginTop + dependency.marginBottom + dependency.height
            return true
        }
        return false
    }

    override fun onDependentViewRemoved(parent: CoordinatorLayout, child: View, dependency: View) {
        if(dependency is Snackbar.SnackbarLayout ) {
            snackbarTranslation = 0
        }
        super.onDependentViewRemoved(parent, child, dependency)
    }
}