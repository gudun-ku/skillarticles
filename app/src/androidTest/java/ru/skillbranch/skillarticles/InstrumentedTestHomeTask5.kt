package ru.skillbranch.skillarticles

import android.graphics.*
import android.text.Layout
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert.assertEquals
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.graphics.drawable.VectorDrawable
import androidx.annotation.ColorInt
import androidx.annotation.Px
import ru.skillbranch.skillarticles.markdown.spans.*


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class InstrumentedTestHometask5 {

    @Test
    fun draw_list_item() {
        // settings
        val color = Color.RED
        val gap = 24f
        val radius = 8f

        // defaults
        val canvasWidth = 700
        val defaultColor = Color.GRAY
        val cml = 0 // current margin location - shift from start of the line
        val ltop = 0 // line top
        val lbase = 60 // line baseline
        val lbottom = 80 // line bottom


        // mocks
        val canvas = mock(Canvas::class.java)
        val paint = mock(Paint::class.java)
        `when` (paint.color).thenReturn(defaultColor)
        val layout = mock(Layout::class.java)

        val text = SpannableString("text")

        val span = UnorderedListSpan(gap, radius, color)
        text.setSpan(span, 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // delay
        Thread.sleep(1_000L)

        // check leading margin assertion
        assertEquals((4 * radius + gap).toInt(), span.getLeadingMargin(true))

        // check bullet draw
        span.drawLeadingMargin(
            canvas, paint, cml, 1,
            ltop, lbase, lbottom, text, 0, text.length,
            true, layout
        )

        // check order call

        val inOrder = inOrder(paint, canvas)
        // check first set color to paint
        inOrder.verify(paint).color = color
        // check draw circle bullet
        inOrder.verify(canvas).drawCircle(
            gap + cml + radius,
            (lbottom - ltop)/2f + ltop,
            radius,
            paint
        )
        // check restore of the paint color
        inOrder.verify(paint).color = defaultColor

    }


    @Test
    fun draw_quoute() {
        // settings
        val color = Color.RED
        val gap = 24f
        val lineWidth = 8f

        // defaults
        val canvasWidth = 700
        val defaultColor = Color.GRAY
        val cml = 0 // current margin location - shift from start of the line
        val ltop = 0 // line top
        val lbase = 60 // line baseline
        val lbottom = 80 // line bottom


        // mocks
        val canvas = mock(Canvas::class.java)
        val paint = mock(Paint::class.java)
        `when` (paint.color).thenReturn(defaultColor)
        val layout = mock(Layout::class.java)

        val text = SpannableString("text")

        val span = BlockquotesSpan(gap,lineWidth, color)
        text.setSpan(span, 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // delay
        Thread.sleep(1_000L)

        assertEquals((lineWidth + gap).toInt(), span.getLeadingMargin(true))


        // check line draw
        span.drawLeadingMargin(
            canvas, paint, cml, 1,
            ltop, lbase, lbottom, text, 0, text.length,
            true, layout
        )

        // check order call

        val inOrder = inOrder(paint, canvas)
        // check first set color to paint
        inOrder.verify(paint).color = color
        inOrder.verify(paint).strokeWidth = lineWidth
        // check draw circle bullet
        inOrder.verify(canvas).drawLine(
            lineWidth/2f,
            ltop.toFloat(),
            lineWidth/2f,
            lbottom.toFloat(),
            paint
        )
        // check restore of the paint color
        inOrder.verify(paint).color = defaultColor

    }

    @Test
    fun draw_header() {
        // settings
        val level = 1
        val textColor = Color.RED
        val lineColor = Color.GREEN
        val marginTop = 24f
        val marginBottom = 16f

        // defaults
        val canvasWidth = 700
        val defaultColor = Color.GRAY
        val cml = 0 // current margin location - shift from start of the line
        val ltop = 0 // line top
        val lbase = 60 // line baseline
        val lbottom = 80 // line bottom


        // mocks
        val canvas = mock(Canvas::class.java)
        `when` (canvas.width).thenReturn(canvasWidth)
        val paint = mock(Paint::class.java)
        `when` (paint.color).thenReturn(defaultColor)
        val measurePaint = mock(TextPaint::class.java)
        val drawPaint = mock(TextPaint::class.java)
        val layout = mock(Layout::class.java)

        val text = SpannableString("text")

        val span = HeaderSpan(level, textColor, lineColor, marginTop, marginBottom)
        text.setSpan(span, 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // delay
        Thread.sleep(1_000L)

        // check leading margin
        assertEquals(0, span.getLeadingMargin(true))
        // check measure state
        span.updateMeasureState(measurePaint)
        verify(measurePaint).textSize *= span.sizes[level]!!
        verify(measurePaint).isFakeBoldText = true

        // check draw state
        span.updateDrawState(drawPaint)
        verify(drawPaint).textSize *= span.sizes[level]!!
        verify(drawPaint).isFakeBoldText = true
        verify(drawPaint).color = textColor

        // check draw line

        // check line draw
        span.drawLeadingMargin(
            canvas, paint, cml, 1,
            ltop, lbase, lbottom, text, 0, text.length,
            true, layout
        )

        val inOrder = inOrder(paint, canvas)

        inOrder.verify(paint).color = lineColor

        val lh = (paint.descent() - paint.ascent())*span.sizes[level]!!
        val lineOffset = lbase + lh*span.linePadding

        inOrder.verify(canvas).drawLine(0f,lineOffset, canvasWidth.toFloat(), lineOffset, paint)

        inOrder.verify(paint).color = defaultColor

    }

    @Test
    fun draw_rule() {
        // settings
        val color = Color.RED
        val width = 24f

        // defaults
        val canvasWidth = 700
        val defaultColor = Color.GRAY
        val cml = 0 // current margin location - shift from start of the line
        val ltop = 0 // line top
        val lbase = 60 // line baseline
        val lbottom = 80 // line bottom


        // mocks
        val canvas = mock(Canvas::class.java)
        `when`(canvas.width).thenReturn(canvasWidth)
        val paint = mock(Paint::class.java)
        `when`(paint.color).thenReturn(defaultColor)
        val measurePaint = mock(TextPaint::class.java)
        val drawPaint = mock(TextPaint::class.java)
        val layout = mock(Layout::class.java)

        val text = SpannableString("text")

        val span = HorizontalRuleSpan(width, color)
        text.setSpan(span, 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // delay
        Thread.sleep(1_000L)

        // check draw rule line
        span.draw(canvas, text, 0, text.length, cml.toFloat(), ltop, lbase, lbottom, paint)

        val inOrder = inOrder(paint, canvas)

        inOrder.verify(paint).color = color
        inOrder.verify(canvas).drawLine(
            0f,
            (ltop + lbottom)/2f,
            canvasWidth.toFloat(),
            (ltop + lbottom)/2f,
            paint
        )
        inOrder.verify(paint).color = defaultColor

    }

    @Test
    fun draw_inline_code() {
        // settings

        val textColor = Color.RED
        val bgColor = Color.GREEN
        val cornerRadius = 8f
        val padding = 8f

        // defaults
        val canvasWidth = 700
        val defaultColor = Color.GRAY
        val measureText = 100f
        val cml = 0 // current margin location - shift from start of the line
        val ltop = 0 // line top
        val lbase = 60 // line baseline
        val lbottom = 80 // line bottom


        // mocks
        val canvas = mock(Canvas::class.java)
        `when`(canvas.width).thenReturn(canvasWidth)
        val paint = mock(Paint::class.java)
        `when`(paint.color).thenReturn(defaultColor)
        `when`(paint.measureText(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyInt(),
            ArgumentMatchers.anyInt()
        )).thenReturn(measureText)
        val layout = mock(Layout::class.java)
        val fm = mock(Paint.FontMetricsInt::class.java)

        val text = SpannableString("text")

        val span = InlineCodeSpan(textColor, bgColor, cornerRadius, padding)
        text.setSpan(span, 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // check measure size
        val size = span.getSize(paint, text, 0, text.length, fm)
        assertEquals((2*padding + measureText).toInt(),size)

        // delay
        Thread.sleep(1_000L)

        // check draw inline code
        span.draw(canvas, text, 0, text.length, cml.toFloat(), ltop, lbase, lbottom, paint)

        val inOrder = inOrder(paint, canvas)

        inOrder.verify(paint).color = bgColor
        inOrder.verify(canvas).drawRoundRect(
            RectF(0f,ltop.toFloat(),measureText + 2*padding, lbottom.toFloat()),
            cornerRadius,
            cornerRadius, paint
        )

        // check draw text
        inOrder.verify(paint).color = textColor
        inOrder.verify(canvas).drawText(text, 0, text.length, cml + padding, lbase.toFloat(), paint)

        inOrder.verify(paint).color = defaultColor
    }

    @Test
    fun draw_link() {
        // settings
        val iconColor = Color.RED
        val padding = 8f
        val textColor = Color.BLUE

        // defaults
        val canvasWidth = 700
        val defaultColor = Color.GRAY
        val measureText = 100f
        val defaultAscent = -30
        val defaultDescent = 10
        val cml = 0 // current margin location - shift from start of the line
        val ltop = 0 // line top
        val lbase = 60 // line baseline
        val lbottom = 80 // line bottom


        // mocks
        val canvas = mock(Canvas::class.java)
        `when`(canvas.width).thenReturn(canvasWidth)
        val paint = mock(Paint::class.java)
        `when`(paint.color).thenReturn(defaultColor)
        `when`(paint.measureText(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyInt(),
            ArgumentMatchers.anyInt()
        )).thenReturn(measureText)
        val fm = mock(Paint.FontMetricsInt::class.java)
        fm.ascent = defaultAscent
        fm.descent = defaultDescent

        // spy
        val linkDrawable: Drawable = spy(VectorDrawable())
        val path: Path = spy(Path())


        val text = SpannableString("text")

        val span = IconLinkSpan(linkDrawable, iconColor,padding,textColor)
        text.setSpan(span, 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        span.path = path

        // check measure size
        val size = span.getSize(paint, text, 0, text.length, fm)
        assertEquals((defaultDescent - defaultAscent + padding + measureText).toInt(),size)

        // delay
        Thread.sleep(1_000L)

        // check drawable set bounds and set tint
        //verify(linkDrawable).setBounds(0,0, fm.descent - fm.ascent, fm.descent - fm.ascent)
        verify(linkDrawable).setTint(iconColor)

        // check draw icon and text
        span.draw(canvas, text, 0, text.length, cml.toFloat(), ltop, lbase, lbottom, paint)

        val inOrder = inOrder(paint,canvas, path, linkDrawable)

        // check path effect
        inOrder.verify(paint, atLeastOnce()).pathEffect = any()
        inOrder.verify(paint, atLeastOnce()).strokeWidth = 0f
        inOrder.verify(paint).color = textColor


        inOrder.verify(path).reset()
        inOrder.verify(path).moveTo(cml + span.iconSize + padding, lbottom.toFloat())
        inOrder.verify(path).lineTo(cml + span.iconSize + padding + span.textWidth, lbottom.toFloat())
        inOrder.verify(canvas).drawPath(path, paint)

        // check draw icon
        inOrder.verify(canvas).save()
        inOrder.verify(canvas).translate(cml.toFloat(), (lbottom - linkDrawable.bounds.bottom).toFloat())
        inOrder.verify(linkDrawable).draw(canvas)
        inOrder.verify(canvas).restore()

        // check draw text
        inOrder.verify(paint).color = textColor
        inOrder.verify(canvas).drawText(text, 0, text.length, cml + span.iconSize + padding,
            lbase.toFloat(), paint)
        inOrder.verify(paint).color = defaultColor

    }

}