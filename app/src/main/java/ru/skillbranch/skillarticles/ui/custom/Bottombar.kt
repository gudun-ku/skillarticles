package ru.skillbranch.skillarticles.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewAnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import com.google.android.material.shape.MaterialShapeDrawable
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.dpToPx
import kotlin.math.hypot
import android.view.animation.TranslateAnimation

class Bottombar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var isHidden = false
    private var centerX: Float = context.dpToPx(200)
    private var centerY: Float = context.dpToPx(96)

    init {
        View.inflate(context, R.layout.layout_bottombar, this)
        val materialBg = MaterialShapeDrawable.createWithElevationOverlay(context)
        materialBg.elevation = elevation
        background = materialBg
    }

    fun show() {
        if (!isHidden || !isAttachedToWindow) return
        isHidden = false
        //animatedShow()
        slideUp()
    }

    fun hide() {
        if (isHidden || !isAttachedToWindow) return
        isHidden = true
        //animatedHide()
        slideDown()
    }

    // slide the view from below itself to the current position
    private fun slideUp() {
        visibility = View.VISIBLE
        val animate = TranslateAnimation(
            0f, // fromXDelta
            0f, // toXDelta
            height.toFloat(), // fromYDelta
            0f
        )                // toYDelta
        animate.duration = 200
        animate.fillAfter = true
        startAnimation(animate)
    }

    // slide the view from its current position to below itself
    private fun slideDown() {
        val animate = TranslateAnimation(
            0f, // fromXDelta
            0f, // toXDelta
            0f, // fromYDelta
            height.toFloat()
        ) // toYDelta
        animate.duration = 200
        animate.fillAfter = true
        startAnimation(animate)
    }
}