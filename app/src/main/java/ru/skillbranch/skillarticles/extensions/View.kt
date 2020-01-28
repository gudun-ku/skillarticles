package ru.skillbranch.skillarticles.extensions

import android.view.View
import android.view.ViewGroup

fun View.setMarginOptionally(left: Int? = null, top: Int? = null,
                             right: Int? = null, bottom: Int? = null)
{
    val viewContext = this.context
    layoutParams<ViewGroup.MarginLayoutParams> {
        left?.let { leftMargin = it }
        top?.let { topMargin =  it }
        right?.let { rightMargin =  it }
        bottom?.let { bottomMargin =  it }
    }
}

inline fun <reified T : ViewGroup.LayoutParams> View.layoutParams(block: T.() -> Unit) {
    if (layoutParams is T) block(layoutParams as T)
}