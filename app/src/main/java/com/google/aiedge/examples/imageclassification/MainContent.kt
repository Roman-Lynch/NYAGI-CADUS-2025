package com.google.aiedge.examples.imageclassification

import androidx.camera.core.ImageProxy
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.aiedge.examples.imageclassification.view.CameraScreen
import com.google.aiedge.examples.imageclassification.view.DevelopmentScreen

enum class Tab {
    Camera, Development
}

@Composable
fun MainContent(
    uiState: UiState,
    modifier: Modifier = Modifier,
    onTabChanged: () -> Unit,
    onImageProxyAnalyzed: (ImageProxy) -> Unit,
) {

    var tab by remember { mutableStateOf(Tab.Camera) }
    fun handleTabChange(newTab: Tab) {
        tab = newTab
        onTabChanged()
    }

    val tabs = Tab.entries
    Column(modifier) {
        TabRow(selectedTabIndex = tab.ordinal) {
            tabs.forEach { t ->
                Tab(
                    text = { Text(t.name, color = Color.White) },
                    selected = tab == t,
                    onClick = { handleTabChange(t) },
                )
            }
        }

        when (tab) {
            Tab.Camera -> CameraScreen(
                uiState = uiState,
                onImageAnalyzed = {
                    onImageProxyAnalyzed(it)
                },
            )
            Tab.Development -> DevelopmentScreen(
                uiState = uiState,
                onImageProxyAnalyzed = {
                    onImageProxyAnalyzed(it)
                }
            )
        }
    }
}