package org.example.app.ui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.example.app.data.RadarPoint

/**
/ PUBLIC_INTERFACE
 * Simple state holder replacing Compose remember to avoid IR inline issues.
 * Stores app UI state in StateFlows that can be collected from Composables.
 */
class AppStateHolder {
    val selectedTab = MutableStateFlow(BottomTab.Dashboard)
    val range = MutableStateFlow(50f)
    val mode = MutableStateFlow("NORMAL")
    val loggingEnabled = MutableStateFlow(false)
    val wifiEnabled = MutableStateFlow(true)
    val mockEnabled = MutableStateFlow(true)

    val points2D = MutableStateFlow<List<RadarPoint>>(emptyList())
    val points3D = MutableStateFlow<List<RadarPoint>>(emptyList())
}
