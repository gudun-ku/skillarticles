package ru.skillbranch.skillarticles.ui.custom.markdown

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.Selection
import android.text.Spannable
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.VisibleForTesting
import androidx.core.view.setPadding
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.attrValue
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.extensions.dpToPx
import ru.skillbranch.skillarticles.extensions.setPaddingOptionally

@SuppressLint("ViewConstructor")
class MarkdownCodeView private constructor(
    context: Context,
    fontSize: Float
) : ViewGroup(context, null, 0), IMarkdownView {
    override var fontSize: Float = fontSize

    override val spannableContent: Spannable

    var copyListener: ((String) -> Unit)? = null

    private lateinit var codeString: CharSequence

    //views
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val iv_copy: ImageView
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val iv_switch: ImageView
    private val tv_codeView: MarkdownTextView
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val sv_scroll: HorizontalScrollView

    //colors
    @ColorInt
    private val darkSurface: Int //darkSurfaceColor
    @ColorInt
    private val darkOnSurface: Int //darkOnSurfaceColor
    @ColorInt
    private val lightSurface: Int //lightSurfaceColor
    @ColorInt
    private val lightOnSurface: Int//lightOnSurfaceColor

    //sizes
    private val iconSize //12dp
    private val radius //8dp
    private val padding //8dp
    private val fadingOffset //144dp
    private val textExtraPadding //80dp
    private val scrollBarHeight //2dp

    //for layout
    private var isSingleLine = false
    private var isDark = false
    private var isManual = false
    private val bgColor

    private val textColor

    init {
        //TODO implement me
    }


    constructor(
        context: Context,
        fontSize: Float,
        code: CharSequence
    ) : this(context, fontSize) {
        //TODO implement me
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //TODO implement me
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        //TODO implement me
    }

    override fun renderSearchPosition(searchPosition: Pair<Int, Int>, offset: Int) {
        //TODO implement me
    }

    private fun toggleColors() {
        //TODO implement me
    }

    private fun applyColors() {
        //TODO implement me
    }
}