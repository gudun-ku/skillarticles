package ru.skillbranch.skillarticles.ui.custom.spans

import android.graphics.*
import android.text.Layout
import android.text.SpannableString
import android.text.style.LeadingMarginSpan
import android.text.style.ReplacementSpan
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting
import ru.skillbranch.skillarticles.data.repositories.Element


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

        val originAscent = paint.ascent()
        val originDescent = paint.descent()

        when (type) {
            Element.BlockCode.Type.SINGLE -> {
                fm.ascent = (originAscent - 2 * padding).toInt()
                fm.descent = (originDescent + 2 * padding).toInt()
            }

            Element.BlockCode.Type.START -> {
                fm.ascent = (originAscent - 2 * padding).toInt()
                fm.descent = originDescent.toInt()
            }

            Element.BlockCode.Type.MIDDLE -> {
                fm.ascent = originAscent.toInt()
                fm.descent = originDescent.toInt()
            }

            Element.BlockCode.Type.END -> {
                fm.ascent = originAscent.toInt()
                fm.descent = (originDescent + 2 * padding).toInt()
            }

        }

        fm.top = fm.ascent
        fm.bottom = fm.descent

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
                    canvas.drawCustomRoundRect(rect, paint, cornerRadius, cornerRadius)
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
                    canvas.drawCustomRoundRect(rect, paint, 0f , 0f ,
                        cornerRadius, cornerRadius)
                }

        }

        paint.forText {
            canvas.drawText(text as SpannableString, start, end, x + padding, y.toFloat(), paint)
        }

    }

    private fun Canvas.drawCustomRoundRect(rect: RectF, paint: Paint, topLeftRadius:Float = 0f,
                                           topRightRadius:Float = 0f, bottomLeftRadius:Float = 0f,
                                           bottomRightRadius:Float = 0f) {
        path.reset()
        path.addRoundRect(
            rect,
            floatArrayOf(
                topLeftRadius, topRightRadius, // Top left radius in px
                topRightRadius, topRightRadius, // Top right radius in px
                bottomRightRadius, bottomRightRadius, // Bottom right radius in px
                bottomLeftRadius, bottomLeftRadius // Bottom left radius in px
            ),
            Path.Direction.CW
        )
        drawPath(path, paint)
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
