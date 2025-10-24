package org.example.app.ui.screens

import android.content.Context
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Dashboard implemented via AndroidView to avoid Compose inline layout calls.
 * Shows connection status, points count, and a mock generator toggle.
 */
// PUBLIC_INTERFACE
@Composable
fun DashboardScreen(
    isConnected: Boolean,
    pointsCount: Int,
    mockEnabled: Boolean,
    onMockToggle: (Boolean) -> Unit
) {
    AndroidView(
        modifier = Modifier,
        factory = { ctx ->
            buildDashboardView(ctx, isConnected, pointsCount, mockEnabled, onMockToggle)
        },
        update = { root ->
            val status = root.findViewWithTag<TextView>("status")
            status.text = if (isConnected) "Connected to broker" else "Disconnected"

            val points = root.findViewWithTag<TextView>("points")
            points.text = "Points received: $pointsCount"

            val mock = root.findViewWithTag<Switch>("mock")
            if (mock.isChecked != mockEnabled) mock.isChecked = mockEnabled
        }
    )
}

private fun buildDashboardView(
    ctx: Context,
    isConnected: Boolean,
    pointsCount: Int,
    mockEnabled: Boolean,
    onMockToggle: (Boolean) -> Unit
): LinearLayout {
    val root = LinearLayout(ctx).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(24, 24, 24, 24)
    }

    val title = TextView(ctx).apply {
        text = "Dashboard"
        textSize = 18f
    }
    root.addView(title)

    val status = TextView(ctx).apply {
        tag = "status"
        text = if (isConnected) "Connected to broker" else "Disconnected"
        textSize = 16f
    }
    root.addView(status)

    val points = TextView(ctx).apply {
        tag = "points"
        text = "Points received: $pointsCount"
        textSize = 16f
    }
    root.addView(points)

    val mockRow = LinearLayout(ctx).apply { orientation = LinearLayout.HORIZONTAL }
    val mockLabel = TextView(ctx).apply {
        text = "Mock generator"
        textSize = 16f
    }
    val mockSwitch = Switch(ctx).apply {
        tag = "mock"
        isChecked = mockEnabled
        setOnCheckedChangeListener { _, checked -> onMockToggle(checked) }
    }
    mockRow.addView(mockLabel)
    mockRow.addView(mockSwitch)
    root.addView(mockRow)

    return root
}
