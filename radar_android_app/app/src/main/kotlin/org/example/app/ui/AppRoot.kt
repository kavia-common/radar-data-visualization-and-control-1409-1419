package org.example.app.ui

import android.content.Context
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.app.data.RadarPoint
import org.example.app.data.RadarPointParser
import org.example.app.mqtt.MqttClientManager
import org.example.app.mqtt.MqttTopics
import org.example.app.ui.components.BottomBar
import org.example.app.ui.components.TopBar
import org.example.app.ui.screens.DashboardScreen
import org.example.app.ui.screens.Plot2DScreen
import org.example.app.ui.screens.Plot3DScreen
import org.example.app.ui.theme.OceanProTheme

// PUBLIC_INTERFACE
enum class BottomTab { Dashboard, Plot2D, Plot3D }

// PUBLIC_INTERFACE
@Composable
fun AppRoot(appContext: Context) {
    OceanProTheme {
        val navController = rememberNavController()

        // Initialize plain Kotlin holders to avoid Compose remember inlining pitfalls
        val stateHolder = AppStateHolder()
        val brokerUri = "tcp://test.mosquitto.org:1883"
        val clientId = "radar-android-sample-" + System.currentTimeMillis()
        val mqttManager = MqttClientManager(appContext, brokerUri, clientId)

        AppContent(navController = navController, mqttManager = mqttManager, stateHolder = stateHolder)
    }
}

@Composable
private fun AppContent(
    navController: NavHostController,
    mqttManager: MqttClientManager,
    stateHolder: AppStateHolder
) {
    val scope = CoroutineScope(Dispatchers.IO)

    // Collect MQTT messages once and update state holder
    DisposableEffect(Unit) {
        val job = scope.launch {
            mqttManager.incomingMessages.collect { (topic, payload) ->
                if (topic == MqttTopics.DATA_POINTS) {
                    val parsed = RadarPointParser.parse(payload)
                    if (parsed.isNotEmpty()) {
                        stateHolder.points2D.value = parsed
                        stateHolder.points3D.value = parsed
                    }
                }
            }
        }
        onDispose {
            job.cancel()
            scope.launch { mqttManager.disconnect() }
        }
    }

    val selectedTab by stateHolder.selectedTab.collectAsState()
    val range by stateHolder.range.collectAsState()
    val mode by stateHolder.mode.collectAsState()
    val loggingEnabled by stateHolder.loggingEnabled.collectAsState()
    val wifiEnabled by stateHolder.wifiEnabled.collectAsState()
    val mockEnabled by stateHolder.mockEnabled.collectAsState()

    val points2D by stateHolder.points2D.collectAsState(initial = samplePoints2D())
    val points3D by stateHolder.points3D.collectAsState(initial = samplePoints3D())

    Scaffold(
        topBar = {
            TopBar(
                range = range,
                onRangeChange = {
                    stateHolder.range.value = it
                    scope.launch {
                        val msg = """{"range": ${it.toInt()}}"""
                        mqttManager.publish(MqttTopics.CMD_RANGE, msg.toByteArray())
                    }
                },
                mode = mode,
                onModeChange = { newMode ->
                    stateHolder.mode.value = newMode
                    scope.launch {
                        val msg = """{"mode": "$newMode"}"""
                        mqttManager.publish(MqttTopics.CMD_MODE, msg.toByteArray())
                    }
                },
                loggingEnabled = loggingEnabled,
                onLoggingToggle = {
                    val newVal = !loggingEnabled
                    stateHolder.loggingEnabled.value = newVal
                    scope.launch {
                        val msg = """{"logging": $newVal}"""
                        mqttManager.publish(MqttTopics.CMD_LOGGING, msg.toByteArray())
                    }
                },
                wifiEnabled = wifiEnabled,
                onWifiToggle = {
                    val newVal = !wifiEnabled
                    stateHolder.wifiEnabled.value = newVal
                    scope.launch {
                        val msg = """{"wifi": $newVal}"""
                        mqttManager.publish(MqttTopics.CMD_WIFI, msg.toByteArray())
                    }
                },
                isConnectedFlow = mqttManager.isConnected,
                onConnectClick = {
                    scope.launch {
                        mqttManager.connectAndSubscribe(listOf(MqttTopics.DATA_POINTS))
                    }
                },
                onDisconnectClick = {
                    scope.launch { mqttManager.disconnect() }
                }
            )
        },
        bottomBar = {
            BottomBar(
                selected = selectedTab,
                onTabSelected = { tab ->
                    stateHolder.selectedTab.value = tab
                    when (tab) {
                        BottomTab.Dashboard -> navController.navigate("dashboard")
                        BottomTab.Plot2D -> navController.navigate("plot2d")
                        BottomTab.Plot3D -> navController.navigate("plot3d")
                    }
                }
            )
        }
    ) { paddingValues ->
        // Use AndroidView host to avoid inline Compose container (Box/Column) IR issues.
        androidx.compose.ui.viewinterop.AndroidView(
            factory = { context ->
                android.widget.FrameLayout(context)
            },
            update = { fl ->
                // no-op; Compose content below
            },
            modifier = androidx.compose.ui.Modifier.padding(paddingValues)
        )

        // Place main content after AndroidView so padding is applied and lint is satisfied
        AppNavHost(
            navController = navController,
            points2D = points2D,
            points3D = points3D,
            isConnectedFlow = mqttManager.isConnected,
            mockEnabled = mockEnabled,
            onMockToggle = { mock -> stateHolder.mockEnabled.value = mock }
        )
    }
}

@Composable
private fun AppNavHost(
    navController: NavHostController,
    points2D: List<RadarPoint>,
    points3D: List<RadarPoint>,
    isConnectedFlow: StateFlow<Boolean>,
    mockEnabled: Boolean,
    onMockToggle: (Boolean) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = "dashboard",
    ) {
        composable("dashboard") {
            val isConnected by isConnectedFlow.collectAsState(initial = false)
            DashboardScreen(
                isConnected = isConnected,
                pointsCount = points2D.size,
                mockEnabled = mockEnabled,
                onMockToggle = onMockToggle
            )
        }
        composable("plot2d") { Plot2DScreen(points = points2D) }
        composable("plot3d") { Plot3DScreen(points = points3D) }
    }
}

private fun samplePoints2D(): List<RadarPoint> =
    List(200) {
        val x = (Math.random() * 20.0 - 10.0).toFloat()
        val y = (Math.random() * 20.0 - 10.0).toFloat()
        RadarPoint(x, y, 0f, intensity = (Math.random()).toFloat())
    }

private fun samplePoints3D(): List<RadarPoint> =
    List(300) {
        val x = (Math.random() * 20.0 - 10.0).toFloat()
        val y = (Math.random() * 20.0 - 10.0).toFloat()
        val z = (Math.random() * 10.0 - 5.0).toFloat()
        RadarPoint(x, y, z, intensity = (Math.random()).toFloat())
    }
