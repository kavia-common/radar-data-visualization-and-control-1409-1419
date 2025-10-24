package org.example.app

import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.setPadding
import org.example.app.ui.AppRoot

// PUBLIC_INTERFACE
class MainActivity : ComponentActivity() {
    /** Entrypoint Activity: hosts a ComposeView inside a FrameLayout to avoid Compose IR inline issues. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val root = FrameLayout(this).apply { setPadding(0) }
        val compose = ComposeView(this).apply {
            setContent {
                AppRoot(appContext = applicationContext)
            }
        }
        root.addView(compose, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ))
        setContentView(root)
    }
}
