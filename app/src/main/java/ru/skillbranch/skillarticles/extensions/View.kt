package ru.skillbranch.skillarticles.extensions

import android.os.Parcelable
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children

fun View.setMarginOptionally(left: Int? = null, top: Int? = null,
                             right: Int? = null, bottom: Int? = null)
{
    layoutParams<ViewGroup.MarginLayoutParams> {
        left?.let { leftMargin = it }
        top?.let { topMargin =  it }
        right?.let { rightMargin =  it }
        bottom?.let { bottomMargin =  it }
    }
}

fun View.setPaddingOptionally(left: Int = paddingLeft, top: Int = paddingTop,
                             right: Int = paddingRight, bottom: Int = paddingBottom)
{
    setPadding(left , top , right, bottom)
}

inline fun <reified T : ViewGroup.LayoutParams> View.layoutParams(block: T.() -> Unit) {
    if (layoutParams is T) block(layoutParams as T)
}

inline fun <reified T: View> View.idTag(idx: Int): String = "cv${T::class.java.simpleName}_${idx}"


fun ViewGroup.saveChildViewStates(): SparseArray<Parcelable> {
    val childViewStates = SparseArray<Parcelable>()
    children.forEach { child -> child.saveHierarchyState(childViewStates) }
    return childViewStates
}

fun ViewGroup.restoreChildViewStates(childViewStates: SparseArray<Parcelable>) {
    children.forEach { child -> child.restoreHierarchyState(childViewStates) }
}