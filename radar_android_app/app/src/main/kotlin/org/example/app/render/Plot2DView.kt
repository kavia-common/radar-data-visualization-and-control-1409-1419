package org.example.app.render

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import org.example.app.data.RadarPoint
import kotlin.math.max

/**
 * Optional classic View-based 2D plot for fallback scenarios (not used by Compose path).
 */
// PUBLIC_INTERFACE
class Plot2DView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLUE }
    private var points: List<RadarPoint> = emptyList()

    fun setPoints(ps: List<RadarPoint>) {
        points = ps
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()

        val xs = points.map { it.x }
        val ys = points.map { it.y }
        val xMin = (xs.minOrNull() ?: -1f) - 0.001f
        val xMax = (xs.maxOrNull() ?: 1f) + 0.001f
        val yMin = (ys.minOrNull() ?: -1f) - 0.001f
        val yMax = (ys.maxOrNull() ?: 1f) + 0.001f

        fun mapX(x: Float) = ((x - xMin) / (xMax - xMin)).coerceIn(0f, 1f) * w
        fun mapY(y: Float) = h - ((y - yMin) / (yMax - yMin)).coerceIn(0f, 1f) * h

        points.forEach { p ->
            val r = max(2f, 2f + p.intensity * 3f)
            canvas.drawCircle(mapX(p.x), mapY(p.y), r, paint)
        }
    }
}
