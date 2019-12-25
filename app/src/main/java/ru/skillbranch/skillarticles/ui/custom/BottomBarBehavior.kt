package ru.skillbranch.skillarticles.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView


class BottomBarBehavior(context: Context, attributeSet: AttributeSet) :
    CoordinatorLayout.Behavior<View>(context,attributeSet) {

    private var oldScrollY = 0

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        return dependency is NestedScrollView
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        if (parent == null || child == null || dependency == null)
            return false

        changeBottomBarState(child as Bottombar, dependency)
        return true
    }

    private fun changeBottomBarState(bottombar: Bottombar, scrollView: View) {
        if (scrollView is NestedScrollView) {

            if (scrollView.scrollY > oldScrollY)
                bottombar.hide()
            else if (scrollView.scrollY < oldScrollY)
                bottombar.show()

            oldScrollY = scrollView.scrollY
        }

    }

}