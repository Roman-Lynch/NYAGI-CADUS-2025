package com.google.aiedge.examples.imageclassification.view

import androidx.compose.foundation.background
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.aiedge.examples.imageclassification.UiState
import com.google.aiedge.examples.imageclassification.language.Language
import com.google.aiedge.examples.imageclassification.pages.BodyRegionsPage
import com.google.aiedge.examples.imageclassification.pages.SettingsPage
import com.google.aiedge.examples.imageclassification.pages.BreastCameraPage

private val horizontalPadding: Dp = 25.dp // standard margins for page
private val standardModifier:Modifier = Modifier.padding(horizontal = horizontalPadding)

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

@Composable
fun HeaderBarButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    filePath: String
) {
    AsyncImage(
        model = "file:///android_asset/${filePath}",
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
    )
}


@Preview
@Composable
fun HeaderBar(setCurrentPage: (Pages) -> Unit = {}) {
    Row(
        modifier = Modifier
            .background(Theme.Teal)
            .fillMaxWidth()
            .height(80.dp)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        HeaderBarButton(filePath = "Icons/BackIcon.png")
        HeaderBarButton(filePath = "Icons/GearIcon.png", onClick = { setCurrentPage(Pages.Settings) })
    }
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

    HeaderBar(setCurrentPage = setCurrentPage)

    // include any modifier that applies to any page here
    val defaultModifier = Modifier.padding(horizontal = Theme.StandardPageMargin)

    when (currentPage) {
        Pages.BodyRegions -> {
            BodyRegionsPage(currentLanguage, defaultModifier, setCurrentPage)
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