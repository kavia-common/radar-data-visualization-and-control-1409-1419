androidApplication {
    namespace = "org.example.app"

    // NOTE: Declarative Gradle DSL restrictions: Only add implementation dependencies here.
    // Compose and AndroidX artifacts added explicitly without altering other blocks.

    dependencies {
        // Kotlin stdlib
        implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")

        // AndroidX core and lifecycle
        implementation("androidx.core:core-ktx:1.13.1")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")

        // Jetpack Compose artifacts (explicit versions)
        implementation("androidx.activity:activity-compose:1.9.2")
        implementation("androidx.compose.ui:ui:1.7.3")
        implementation("androidx.compose.ui:ui-tooling-preview:1.7.3")
        implementation("androidx.compose.material3:material3:1.3.0")

        // Navigation for Compose
        implementation("androidx.navigation:navigation-compose:2.8.2")

        // Coroutines
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

        // MQTT client (HiveMQ MQTT Client)
        implementation("com.hivemq:hivemq-mqtt-client:1.3.4")

        // Optional JSON parsing for mock/data handling
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

        // Unit testing (JUnit 4) to ensure discovery works
        implementation("junit:junit:4.13.2")

        // Existing sample dependencies kept for build compatibility
        implementation("org.apache.commons:commons-text:1.11.0")
        implementation(project(":utilities"))
    }
}
