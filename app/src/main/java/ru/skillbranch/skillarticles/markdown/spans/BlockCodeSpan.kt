package ru.skillbranch.skillarticles.markdown.spans

import android.graphics.*
import android.text.Layout
import android.text.SpannableString
import android.text.style.LeadingMarginSpan
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
) : LeadingMarginSpan, ReplacementSpan() {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var rect = RectF()
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var path = Path()

    private var originAscent = 0
    private var originDescent = 0

    override fun getLeadingMargin(first: Boolean): Int {
        return 0
    }

    override fun drawLeadingMargin(
        canvas: Canvas, paint: Paint, currentMarginLocation: Int, paragraphDirection: Int,
        lineTop: Int, lineBaseline: Int, lineBottom: Int, text: CharSequence?, lineStart: Int,
        lineEnd: Int, isFirstLine: Boolean, layout: Layout?
    ) {
        //canvas.drawFontLines(lineTop, lineBottom, lineBaseline, paint)
    }

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        fm ?: return 0

        originAscent = paint.ascent().toInt()
        originDescent = paint.descent().toInt()

        when (type) {
            Element.BlockCode.Type.SINGLE -> {
                fm.ascent = (originAscent *0.85 - 2 * padding).toInt()
                fm.descent = (originDescent*0.85 + 2 * padding).toInt()
            }

            Element.BlockCode.Type.START -> {
                fm.ascent = (originAscent*0.85  - 2 * padding).toInt()
                fm.descent = (originDescent*0.85).toInt()
            }

            Element.BlockCode.Type.MIDDLE -> {
                fm.ascent = (originAscent*0.85).toInt()
                fm.descent = (originDescent*0.85).toInt()
            }

            Element.BlockCode.Type.END -> {
                fm.ascent = (originAscent*0.85).toInt()
                fm.descent = (originDescent*0.85 + 2 * padding).toInt()
            }
        }
        return 0
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
                        x + canvas.width , bottom.toFloat())
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
                    rect.set(0f , top.toFloat(), x + canvas.width,
                        bottom.toFloat() - padding)
                    canvas.drawCornerRoundRect(rect, paint, 0f , 0f ,
                        cornerRadius, cornerRadius)
                }

        }

        paint.forText {
            canvas.drawText(text as SpannableString, start, end, x + padding, y.toFloat(), paint)
        }

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

        textSize *= 0.85f
        color = textColor
        typeface = Typeface.create(Typeface.MONOSPACE, oldStyle)

        block()

        color = oldColor
        typeface = oldFont
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

    // helper function
    private fun Canvas.drawFontLines(
        top: Int,
        bottom: Int,
        lineBaseline: Int,
        paint: Paint
    ) {
        // top font line
        drawLine(0f, top + 0f, width + 0f, top + 0f, Paint().apply { color = Color.RED })
        // bottom font line
        drawLine(0f, bottom + 0f, width + 0f, bottom + 0f, Paint().apply { color = Color.GREEN })
        // baseline
        drawLine(0f, lineBaseline + 0f, width + 0f, lineBaseline + 0f, Paint().apply { color = Color.BLACK })
        // ascent
        drawLine(0f, paint.ascent() + lineBaseline + 0f, width + 0f, paint.ascent() + lineBaseline + 0f, Paint().apply { color = Color.CYAN })
        // descent
        drawLine(0f, paint.descent() + lineBaseline + 0f, width + 0f, paint.descent() + lineBaseline + 0f, Paint().apply { color = Color.MAGENTA })

    }

}
