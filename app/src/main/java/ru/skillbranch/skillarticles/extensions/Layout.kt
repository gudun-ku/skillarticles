package ru.skillbranch.skillarticles.extensions

import android.text.Layout

/**
 * Get the height of a line
 */
fun Layout.getLineHeight(line: Int): Int {
    return getLineTop(line.inc()) - getLineTop(line)
}

/**
 * Returns top of the Layout after removing extra padding applied by the Layout
 */
fun Layout.getLineBottomWithouPadding(line: Int): Int {
    var lineBottom = getLineBottomWithoutSpacing(line)
    if (line == lineCount.dec()) {
        lineBottom -= bottomPadding
    }
    return lineBottom
}

/**
 * Returns bottom of the Layout after removing extra padding applied by the Layout
 */
fun Layout.getLineTopWithoutPadding(line: Int): Int {
    var lineTop = getLineTop(line)
    if (line == 0) {
        lineTop -= topPadding
    }
    return lineTop
}

/**
 * Return line bottom without spacing
 */
fun Layout.getLineBottomWithoutSpacing(line: Int): Int {
    val lineBottom = getLineBottom(line)
    val isLastLine = line == lineCount.dec()
    val hasLineSpacing = spacingAdd != 0f

    return if (!hasLineSpacing || isLastLine) {
        lineBottom + spacingAdd.toInt()
    } else {
        lineBottom - spacingAdd.toInt()
    }
}
