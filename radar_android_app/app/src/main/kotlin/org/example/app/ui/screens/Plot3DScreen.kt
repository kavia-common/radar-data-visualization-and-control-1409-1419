package org.example.app.ui.screens

import android.opengl.GLSurfaceView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.example.app.data.RadarPoint
import org.example.app.render.Plot3DRenderer

/**
 * 3D plot using GLSurfaceView hosted via AndroidView. No Compose remember calls to avoid IR inline issues.
 */
// PUBLIC_INTERFACE
@Composable
fun Plot3DScreen(points: List<RadarPoint>) {
    AndroidView(
        modifier = Modifier,
        factory = { context ->
            val renderer = Plot3DRenderer()
            GLSurfaceView(context).apply {
                setEGLContextClientVersion(2)
                setRenderer(renderer)
                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
                // Store renderer as tag to update later
                setTag(renderer)
            }
        },
        update = { glView ->
            val renderer = glView.getTag() as? Plot3DRenderer
            renderer?.updatePoints(points)
        }
    )
}
