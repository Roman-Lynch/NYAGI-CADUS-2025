package com.google.aiedge.examples.imageclassification.view

import androidx.camera.core.ImageProxy
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.aiedge.examples.imageclassification.UiState
import com.google.aiedge.examples.imageclassification.language.Language
import com.google.aiedge.examples.imageclassification.navigation.HeaderBar
import com.google.aiedge.examples.imageclassification.navigation.NavigationStack
import com.google.aiedge.examples.imageclassification.pages.BodyRegionsPage
import com.google.aiedge.examples.imageclassification.pages.SettingsPage
import com.google.aiedge.examples.imageclassification.pages.BreastCameraPage

@Composable
fun DefaultAlert(onClick: () -> Unit) {

    AlertDialog(
        onDismissRequest = {},
        title = { Text("Setting Button Test") },
        text = { Text("Settings Button Is Working") },
        buttons = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally, // Optional: Centers the buttons horizontally
                verticalArrangement = Arrangement.spacedBy(16.dp), // Optional: Adds space between the buttons
                modifier = Modifier.padding(16.dp) // Optional: Adds padding around the column
            ) {
                TextButton(onClick = onClick) {
                    Text("Ok")
                }
                TextButton(onClick = onClick) {
                    Text("Cancel")
                }
            }
        }
    )
}

enum class Pages {
    BodyRegions, ScanType, Scan, Settings
}

@Composable
fun DevelopmentScreen(uiState: UiState, onImageProxyAnalyzed: (ImageProxy) -> Unit) {

    var currentPage by remember { mutableStateOf(Pages.BodyRegions) }
    val setCurrentPage = { page: Pages -> currentPage = page}

    var currentLanguage by remember { mutableStateOf(Language.English) }
    val setLanguage = { language: Language -> currentLanguage = language }

    val navigationStack by remember { mutableStateOf(NavigationStack(setCurrentPage, Pages.BodyRegions))}

    HeaderBar(navigationStack, currentLanguage)

    val defaultModifier = Modifier.padding(horizontal = Theme.StandardPageMargin)

    when (currentPage) {
        Pages.BodyRegions -> {
            BodyRegionsPage(currentLanguage, defaultModifier, navigationStack)
        }
        Pages.ScanType -> {

        }
        Pages.Scan -> {
            BreastCameraPage(
                uiState,
                currentLanguage,
                modifier = Modifier.fillMaxWidth(),
                onImageAnalyzed = onImageProxyAnalyzed,
            )
        }
        Pages.Settings -> {
            SettingsPage(currentLanguage, setLanguage, defaultModifier)
        }
    }
}