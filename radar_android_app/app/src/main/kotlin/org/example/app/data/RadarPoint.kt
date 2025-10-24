package org.example.app.data

/** A single radar point in 3D space, with optional intensity. */
// PUBLIC_INTERFACE
data class RadarPoint(
    val x: Float,
    val y: Float,
    val z: Float,
    val intensity: Float = 1f
)
