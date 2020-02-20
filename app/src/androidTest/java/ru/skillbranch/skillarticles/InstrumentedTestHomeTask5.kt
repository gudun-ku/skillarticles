package ru.skillbranch.skillarticles

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.Layout
import android.text.SpannableString
import android.text.Spanned
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert.assertEquals
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.mockito.Mockito.*
import ru.skillbranch.skillarticles.markdown.spans.UnorderedListSpan

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
        Thread.sleep(2_000L)

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

}