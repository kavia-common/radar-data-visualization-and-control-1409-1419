package org.example.app.ui.components

import android.content.Context
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.flow.StateFlow

/**
 * TopBar implemented using a lightweight AndroidView to avoid Compose inline layout calls.
 * Exposes the same controls: range slider, mode (basic), logging and wifi toggles, connect/disconnect buttons.
 */
// PUBLIC_INTERFACE
@Composable
fun TopBar(
    range: Float,
    onRangeChange: (Float) -> Unit,
    mode: String,
    onModeChange: (String) -> Unit,
    loggingEnabled: Boolean,
    onLoggingToggle: () -> Unit,
    wifiEnabled: Boolean,
    onWifiToggle: () -> Unit,
    isConnectedFlow: StateFlow<Boolean>,
    onConnectClick: () -> Unit,
    onDisconnectClick: () -> Unit
) {
    val isConnected by isConnectedFlow.collectAsState(initial = false)

    AndroidView(
        modifier = Modifier,
        factory = { ctx ->
            buildTopBarView(
                ctx = ctx,
                range = range,
                onRangeChange = onRangeChange,
                mode = mode,
                onModeChange = onModeChange,
                loggingEnabled = loggingEnabled,
                onLoggingToggle = onLoggingToggle,
                wifiEnabled = wifiEnabled,
                onWifiToggle = onWifiToggle,
                isConnected = isConnected,
                onConnectClick = onConnectClick,
                onDisconnectClick = onDisconnectClick
            )
        },
        update = { layout ->
            val title = layout.findViewWithTag<TextView>("title")
            title.text = "Ocean RADAR"

            val status = layout.findViewWithTag<TextView>("status")
            status.text = if (isConnected) "Connected" else "Disconnected"

            val seek = layout.findViewWithTag<SeekBar>("range")
            val newProgress = range.toInt().coerceIn(10, 200)
            if (seek.progress != newProgress) seek.progress = newProgress

            val logSwitch = layout.findViewWithTag<Switch>("log")
            if (logSwitch.isChecked != loggingEnabled) logSwitch.isChecked = loggingEnabled

            val wifiSwitch = layout.findViewWithTag<Switch>("wifi")
            if (wifiSwitch.isChecked != wifiEnabled) wifiSwitch.isChecked = wifiEnabled

            val connectBtn = layout.findViewWithTag<Button>("connect")
            val disconnectBtn = layout.findViewWithTag<Button>("disconnect")
            connectBtn.isEnabled = !isConnected
            disconnectBtn.isEnabled = isConnected
        }
    )
}

private fun buildTopBarView(
    ctx: Context,
    range: Float,
    onRangeChange: (Float) -> Unit,
    mode: String,
    onModeChange: (String) -> Unit,
    loggingEnabled: Boolean,
    onLoggingToggle: () -> Unit,
    wifiEnabled: Boolean,
    onWifiToggle: () -> Unit,
    isConnected: Boolean,
    onConnectClick: () -> Unit,
    onDisconnectClick: () -> Unit
): LinearLayout {
    val root = LinearLayout(ctx).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(24, 24, 24, 12)
    }

    val header = LinearLayout(ctx).apply {
        orientation = LinearLayout.HORIZONTAL
    }
    val title = TextView(ctx).apply {
        text = "Ocean RADAR"
        textSize = 18f
        tag = "title"
    }
    val status = TextView(ctx).apply {
        text = if (isConnected) "Connected" else "Disconnected"
        tag = "status"
        setPadding(16, 0, 0, 0)
    }
    header.addView(title)
    header.addView(status)
    root.addView(header)

    val rangeLabel = TextView(ctx).apply {
        text = "Range: ${range.toInt()} m"
        textSize = 14f
    }
    root.addView(rangeLabel)

    val seek = SeekBar(ctx).apply {
        max = 200
        progress = range.toInt().coerceIn(10, 200)
        tag = "range"
        setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                rangeLabel.text = "Range: $progress m"
                if (fromUser) onRangeChange(progress.toFloat())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
    root.addView(seek)

    val row = LinearLayout(ctx).apply { orientation = LinearLayout.HORIZONTAL }

    val modeLabel = TextView(ctx).apply {
        text = "Mode: $mode"
        setPadding(0, 0, 16, 0)
    }
    val modeBtn = Button(ctx).apply {
        text = "Change"
        setOnClickListener {
            val next = when (mode) {
                "NORMAL" -> "TRACK"
                "TRACK" -> "DEMO"
                else -> "NORMAL"
            }
            onModeChange(next)
        }
    }
    val logSwitch = Switch(ctx).apply {
        text = "Log"
        isChecked = loggingEnabled
        tag = "log"
        setOnCheckedChangeListener { _, _ -> onLoggingToggle() }
    }
    val wifiSwitch = Switch(ctx).apply {
        text = "Wi‑Fi"
        isChecked = wifiEnabled
        tag = "wifi"
        setOnCheckedChangeListener { _, _ -> onWifiToggle() }
    }
    val connectBtn = Button(ctx).apply {
        text = "Connect"
        tag = "connect"
        isEnabled = !isConnected
        setOnClickListener { onConnectClick() }
    }
    val disconnectBtn = Button(ctx).apply {
        text = "Disconnect"
        tag = "disconnect"
        isEnabled = isConnected
        setOnClickListener { onDisconnectClick() }
    }

    row.addView(modeLabel)
    row.addView(modeBtn)
    row.addView(logSwitch)
    row.addView(wifiSwitch)
    row.addView(connectBtn)
    row.addView(disconnectBtn)

    root.addView(row)

    return root
}
