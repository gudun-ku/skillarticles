package ru.skillbranch.skillarticles.markdown.spans

import android.graphics.*
import android.text.Spanned
import android.text.style.LineHeightSpan
import android.text.style.ReplacementSpan
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting
import ru.skillbranch.skillarticles.markdown.Element


class BlockCodeSpan(
    @ColorInt
    private val textColor: Int,
    @ColorInt
    private val bgColor: Int,
    @Px
    private val cornerRadius: Float,
    @Px
    private val padding: Float,
    private val type: Element.BlockCode.Type
) : LineHeightSpan,ReplacementSpan() {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var rect = RectF()
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var path = Path()

    private var originAscent = 0
    private var originDescent = 0
    private var originTop = 0
    private var originBottom = 0
    private var originHeight = 0

    override fun chooseHeight(
        text: CharSequence?,
        start: Int,
        end: Int,
        spanstartv: Int,
        lineHeight: Int,
        fm: Paint.FontMetricsInt?
    ) {
        fm ?: return
        text as Spanned

        originAscent = fm.ascent
        originDescent = fm.descent
        originTop = fm.top
        originBottom = fm.bottom
        originHeight = fm.descent - originAscent

        when (type) {
            Element.BlockCode.Type.SINGLE -> {
                //check lineHeight SINGLE type
                fm.ascent = (originAscent * 0.85f - 2 * padding).toInt()
                fm.descent = (originDescent * 0.85f + 2 * padding).toInt()
                fm.top = originTop + fm.ascent
                fm.bottom = originBottom + fm.descent
            }

            Element.BlockCode.Type.START -> {
                //check lineHeight SINGLE type
                fm.ascent = (originAscent * 0.85f - 2 * padding).toInt()
                fm.descent = (originAscent * 0.85f).toInt()
                fm.top = originTop + fm.ascent
                fm.bottom = originBottom + fm.descent
            }

            Element.BlockCode.Type.MIDDLE -> {
                //check lineHeight SINGLE type
                fm.ascent = (originAscent * 0.85f).toInt()
                fm.descent = (originAscent * 0.85f).toInt()
                fm.top = originTop + fm.ascent
                fm.bottom = originBottom + fm.descent
            }

            Element.BlockCode.Type.END -> {
                //check lineHeight SINGLE type
                fm.ascent = (originAscent * 0.85f).toInt()
                fm.descent = (originDescent * 0.85f + 2 * padding).toInt()
                fm.top = originTop + fm.ascent
                fm.bottom = originBottom + fm.descent
            }
        }
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        when(type) {
            Element.BlockCode.Type.SINGLE ->
                paint.forBackground {
                    rect.set(0f , top.toFloat() + padding, x + canvas.width
                        , bottom.toFloat() - padding)
                    canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
                }

            Element.BlockCode.Type.START ->
                paint.forBackground {
                    rect.set(0f , top.toFloat() + padding,
                        x + canvas.width , bottom.toFloat() - originDescent )
                    canvas.drawCornerRoundRect(rect, paint, cornerRadius, cornerRadius)
                }

            Element.BlockCode.Type.MIDDLE ->
                paint.forBackground {
                    rect.set(0f , top.toFloat(), x + canvas.width ,
                        bottom.toFloat())
                    canvas.drawRect(rect, paint)
                }

            Element.BlockCode.Type.END ->
                paint.forBackground {
                    rect.set(0f , top.toFloat() + originAscent, x + canvas.width,
                        bottom.toFloat() - padding)
                    canvas.drawCornerRoundRect(rect, paint, 0f , 0f ,
                        cornerRadius, cornerRadius)
                }

        }

        paint.forText {
            canvas.drawText(text, start, end, x + padding, y.toFloat(), paint)
        }
    }

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        return end - start
    }

    private fun Canvas.drawCornerRoundRect(rect: RectF, paint: Paint, topLeftRadius:Float = 0f,
                                           topRightRadius:Float = 0f, bottomLeftRadius:Float = 0f,
                                           bottomRightRadius:Float = 0f) {

        path.reset()
        path.moveTo(rect.left + topLeftRadius, rect.top)
        path.lineTo(rect.right - topRightRadius, rect.top)
        path.quadTo(rect.right, rect.top, rect.right, rect.top + topRightRadius)
        path.lineTo(rect.right, rect.bottom - bottomRightRadius)
        path.quadTo(rect.right, rect.bottom, rect.right - bottomRightRadius, rect.bottom)
        path.lineTo(rect.left + bottomLeftRadius, rect.bottom)
        path.quadTo(rect.left, rect.bottom, rect.left, rect.bottom - bottomLeftRadius)
        path.lineTo(rect.left, rect.top + topLeftRadius)
        path.quadTo(rect.left, rect.top, rect.left + topLeftRadius / 2, rect.top)
        drawPath(path, paint)
        path.close()

    }

    private inline fun Paint.forText(block: () -> Unit) {
        val oldSize = textSize
        val oldStyle = typeface?.style ?: 0
        val oldFont = typeface
        val oldColor = color

        color = textColor
        typeface = Typeface.create(Typeface.MONOSPACE, oldStyle)
        textSize *= 0.85f

        block()

        color = oldColor
        typeface= oldFont
        textSize = oldSize
    }

    private inline fun Paint.forBackground(block: () -> Unit) {
        val oldColor = color
        val oldStyle = style
        val oldPath = path

        color = bgColor
        style = Paint.Style.FILL

        block()

        color = oldColor
        style = oldStyle
        path = oldPath
    }
}
