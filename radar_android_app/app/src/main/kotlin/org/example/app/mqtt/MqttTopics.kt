package org.example.app.mqtt

/** Topic constants used by the app to publish/subscribe MQTT messages. */
// PUBLIC_INTERFACE
object MqttTopics {
    const val DATA_POINTS = "radar/points"
    const val CMD_RANGE = "radar/cmd/range"
    const val CMD_MODE = "radar/cmd/mode"
    const val CMD_LOGGING = "radar/cmd/logging"
    const val CMD_WIFI = "radar/cmd/wifi"
}
