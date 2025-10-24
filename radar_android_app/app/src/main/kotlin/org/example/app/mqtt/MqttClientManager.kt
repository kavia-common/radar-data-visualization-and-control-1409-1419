package org.example.app.mqtt

import android.content.Context
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.MqttGlobalPublishFilter
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

/**
 * MQTT client manager using HiveMQ Client library.
 * Provides StateFlow for connection status and SharedFlow for incoming messages.
 */
// PUBLIC_INTERFACE
class MqttClientManager(
    private val context: Context,
    private val brokerUri: String,
    private val clientId: String
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    // (topic, payloadString)
    private val _incomingMessages = MutableSharedFlow<Pair<String, String>>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val incomingMessages: SharedFlow<Pair<String, String>> = _incomingMessages

    private var client: Mqtt3AsyncClient? = null

    // PUBLIC_INTERFACE
    suspend fun connectAndSubscribe(topics: List<String>) {
        try {
            if (client == null) {
                client = MqttClient.builder()
                    .useMqttVersion3()
                    .identifier(clientId)
                    .serverHost(parseHost(brokerUri))
                    .serverPort(parsePort(brokerUri))
                    .buildAsync()
            }
            val c = client ?: return
            c.connectWith()
                .cleanSession(true)
                .send()
                .get(5, TimeUnit.SECONDS)

            _isConnected.value = true

            // Set up global listener
            c.publishes(MqttGlobalPublishFilter.ALL) { pub ->
                val topic = pub.topic.toString()
                val payload = pub.payload.orElse(null)
                val text = payload?.let { String(it.array(), StandardCharsets.UTF_8) } ?: ""
                scope.launch {
                    _incomingMessages.emit(topic to text)
                }
            }

            // Subscribe to topics
            topics.forEach { t ->
                c.subscribeWith()
                    .topicFilter(t)
                    .qos(MqttQos.AT_LEAST_ONCE)
                    .send()
            }
        } catch (ex: Exception) {
            _isConnected.value = false
        }
    }

    // PUBLIC_INTERFACE
    suspend fun publish(topic: String, payload: ByteArray, qos: MqttQos = MqttQos.AT_MOST_ONCE, retain: Boolean = false) {
        try {
            client?.publishWith()
                ?.topic(topic)
                ?.payload(payload)
                ?.qos(qos)
                ?.retain(retain)
                ?.send()
        } catch (_: Exception) {
            // ignore publish error in demo
        }
    }

    // PUBLIC_INTERFACE
    suspend fun disconnect() {
        try {
            client?.disconnect()
        } catch (_: Exception) {
        } finally {
            _isConnected.value = false
            client = null
        }
    }

    private fun parseHost(uri: String): String {
        // e.g., tcp://host:port
        return uri.substringAfter("://").substringBefore(":")
    }

    private fun parsePort(uri: String): Int {
        return uri.substringAfterLast(":").toIntOrNull() ?: 1883
    }
}
