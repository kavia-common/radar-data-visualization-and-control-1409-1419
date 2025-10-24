package org.example.app.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import org.example.app.ui.BottomTab
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings

/**
 * Bottom navigation for Dashboard, 2D, and 3D tabs.
 */
// PUBLIC_INTERFACE
@Composable
fun BottomBar(selected: BottomTab, onTabSelected: (BottomTab) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            selected = selected == BottomTab.Dashboard,
            onClick = { onTabSelected(BottomTab.Dashboard) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
            label = { Text("Dashboard") }
        )
        NavigationBarItem(
            selected = selected == BottomTab.Plot2D,
            onClick = { onTabSelected(BottomTab.Plot2D) },
            icon = { Icon(Icons.Default.List, contentDescription = "2D") },
            label = { Text("2D") }
        )
        NavigationBarItem(
            selected = selected == BottomTab.Plot3D,
            onClick = { onTabSelected(BottomTab.Plot3D) },
            icon = { Icon(Icons.Default.Settings, contentDescription = "3D") },
            label = { Text("3D") }
        )
    }
}
