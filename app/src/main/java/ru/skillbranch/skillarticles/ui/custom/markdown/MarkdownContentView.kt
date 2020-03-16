package ru.skillbranch.skillarticles.ui.custom.markdown

import android.content.Context
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.forEachIndexed
import androidx.core.view.size
import kotlinx.android.synthetic.main.activity_root.view.*
import ru.skillbranch.skillarticles.data.repositories.MarkdownElement
import ru.skillbranch.skillarticles.extensions.*
import kotlin.properties.Delegates


class MarkdownContentView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {
    private lateinit var elements: List<MarkdownElement>

    // for restore state
    private var ids = arrayListOf<Int>()

    companion object {
        private const val SPARSE_STATE_KEY = "SPARSE_STATE_KEY"
        private const val SUPER_STATE_KEY = "SUPER_STATE_KEY"
    }


    var textSize by Delegates.observable(14f) {_, old, value ->
        if (value == old) return@observable
        this.children.forEach {
            it as IMarkdownView
            it.fontSize = value
        }
    }

    var isLoading: Boolean = true
    val padding = context.dpToIntPx(8)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var usedHeight = paddingTop
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)

        children.forEach {
            measureChild(it, widthMeasureSpec, heightMeasureSpec)
            usedHeight += it.measuredHeight
        }

        usedHeight += paddingBottom
        setMeasuredDimension(width, usedHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var usedHeight = paddingTop
        val bodyWidth = right - left - paddingLeft - paddingRight
        val left = paddingLeft
        val right = paddingLeft + bodyWidth

        children.forEach {
            if (it is MarkdownTextView) {
                it.layout(
                    left - paddingLeft / 2,
                    usedHeight,
                    r - paddingRight / 2,
                    usedHeight + it.measuredHeight
                )
            } else {
                it.layout(
                    left,
                    usedHeight,
                    right,
                    usedHeight + it.measuredHeight
                )
            }
            usedHeight += it.measuredHeight
        }
    }

    fun setContent(content: List<MarkdownElement>) {
        elements = content
        content.forEachIndexed { idx, it ->
            when (it) {
                is MarkdownElement.Text -> {
                    val tv = MarkdownTextView(context, textSize).apply {
                        setPaddingOptionally(left = padding, right = padding)
                        setLineSpacing(fontSize * 0.5f, 1f)
                    }

                    MarkdownBuilder(context)
                        .markdownToSpan(it)
                        .run {
                            tv.setText(this, TextView.BufferType.SPANNABLE)
                        }

//                    tv.id = if (ids[idx] == -1) View.generateViewId() else ids[idx]
//                    ids[idx] = tv.id
                    addView(tv)
                }

                is MarkdownElement.Image -> {
                    val iv = MarkdownImageView(
                        context,
                        textSize,
                        it.image.url,
                        it.image.text,
                        it.image.alt
                    )
//                    iv.id = if (ids[idx] == -1) View.generateViewId() else ids[idx]
//                    ids[idx] = iv.id
                    addView(iv)
                }

                is MarkdownElement.Scroll -> {
                    val sv = MarkdownCodeView(
                        context,
                        textSize,
                        it.blockCode.text
                    )
//                    sv.id = if (ids[idx] == -1) View.generateViewId() else ids[idx]
//                    ids[idx] = sv.id
                    addView(sv)

                }
            }
        }
    }

    fun renderSearchResult(searchResult: List<Pair<Int, Int>>) {
        children.forEach {view ->
            view as IMarkdownView
            view.clearSearchResult()
        }

        if(searchResult.isEmpty()) return

        val bounds = elements.map { it.bounds }
        val result = searchResult.groupByBounds(bounds)

        children.forEachIndexed { index, view ->
            view as IMarkdownView
            // search for child view with markdown element offset
            view.renderSearchResult(result[index], elements[index].offset)
        }

    }

    fun renderSearchPosition(
        searchPosition: Pair<Int, Int>?,
        force: Boolean = false
    ) {
        searchPosition ?: return
        val bounds = elements.map { it.bounds }
        val index = bounds.indexOfFirst { (start, end) ->
            val boundRange = start..end
            val (startPos, endPos) = searchPosition
            startPos in boundRange && endPos in boundRange
        }

        if (index == -1) return
        val view = getChildAt(index)
        view as IMarkdownView
        view.renderSearchPosition(searchPosition, elements[index].offset)
    }


    fun clearSearchResult() {
        children.forEach { view ->
            view as IMarkdownView
            view.clearSearchResult()
        }
    }

    fun setCopyListener(listener: (String) -> Unit) {
        children.filterIsInstance<MarkdownCodeView>()
            .forEach { it.copyListener = listener }

    }

    public override fun onSaveInstanceState(): Parcelable? {
        return SavedState(super.onSaveInstanceState()).apply {

            ids = IntArray(tv_text_content.size).apply {
                fill(-1, 0, size)
            }.toCollection(ArrayList())
            tv_text_content.forEachIndexed { idx, view ->
               if (view.id < 0) view.id = View.generateViewId()
               ids[idx] = view.id
            }

            savedIds = ids
            childrenStates = saveChildViewStates()
        }
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        when (state) {
            is SavedState -> {
                super.onRestoreInstanceState(state.superState)
                ids = state.savedIds
                tv_text_content.forEachIndexed { idx, view ->
                    if (view.id < 0) view.id = ids[idx]
                }

                state.childrenStates?.let { restoreChildViewStates(it) }
            }
            else -> super.onRestoreInstanceState(state)
        }
    }

    internal class SavedState : BaseSavedState {

        var savedIds = arrayListOf<Int>()

        var childrenStates: SparseArray<Parcelable>? = null

        constructor(superState: Parcelable?) : super(superState)

        constructor(src: Parcel) : super(src) {
            val idsArray = intArrayOf()
            src.readIntArray(idsArray)
            savedIds = idsArray.toCollection(ArrayList())
            childrenStates = src.readSparseArray(javaClass.classLoader)
        }

        override fun writeToParcel(dst: Parcel, flags: Int) {
            super.writeToParcel(dst, flags)
            dst.writeIntArray(savedIds.toIntArray())
            dst.writeSparseArray(childrenStates as SparseArray<Any>)
        }

        companion object {
            @JvmField
            val CREATOR = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(source: Parcel) = SavedState(source)
                override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
            }
        }
    }

}
