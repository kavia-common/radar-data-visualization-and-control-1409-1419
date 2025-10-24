package org.example.app.ui.screens

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.example.app.data.RadarPoint
import kotlin.math.max

/**
 * 2D scatter plot implemented as a classic Android View to avoid Compose inline layout calls.
 */
// PUBLIC_INTERFACE
@Composable
fun Plot2DScreen(points: List<RadarPoint>) {
    AndroidView(
        modifier = Modifier,
        factory = { ctx ->
            Scatter2DView(ctx).apply { setPoints(points) }
        },
        update = { view ->
            view.setPoints(points)
        }
    )
}

private class Scatter2DView(context: Context) : View(context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#2563EB")
    }
    private var points: List<RadarPoint> = emptyList()

    fun setPoints(ps: List<RadarPoint>) {
        points = ps
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat().coerceAtLeast(1f)
        val h = height.toFloat().coerceAtLeast(1f)
        val xs = points.map { it.x }
        val ys = points.map { it.y }
        val xMin = (xs.minOrNull() ?: -1f) - 0.001f
        val xMax = (xs.maxOrNull() ?: 1f) + 0.001f
        val yMin = (ys.minOrNull() ?: -1f) - 0.001f
        val yMax = (ys.maxOrNull() ?: 1f) + 0.001f

        fun mapX(x: Float) = ((x - xMin) / (xMax - xMin)).coerceIn(0f, 1f) * w
        fun mapY(y: Float) = h - ((y - yMin) / (yMax - yMin)).coerceIn(0f, 1f) * h

        for (p in points) {
            val r = max(2f, 2f + p.intensity * 3f)
            canvas.drawCircle(mapX(p.x), mapY(p.y), r, paint)
        }
    }
}
