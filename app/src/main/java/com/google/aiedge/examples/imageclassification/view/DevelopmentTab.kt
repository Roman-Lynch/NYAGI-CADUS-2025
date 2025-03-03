package com.google.aiedge.examples.imageclassification.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.aiedge.examples.imageclassification.language.Language
import com.google.aiedge.examples.imageclassification.pages.BodyRegionsPage
import com.google.aiedge.examples.imageclassification.pages.SettingsPage

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


@Preview
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
                .size(70.dp)
                .clickable(onClick = onClick)
        )
}

@Preview
@Composable
fun HeaderBar(setCurrentPage: (Pages) -> Unit = {}) {

    var textHeight by remember { mutableStateOf(0) }
    val density = LocalDensity.current.density


    Row(modifier = Modifier
        .background(Theme.Teal)
        .fillMaxWidth()
        .height(80.dp)
        .padding(horizontal = Theme.StandardPageMargin)
        .onGloballyPositioned { coordinates -> textHeight = coordinates.size.height },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        HeaderBarButton(filePath = "Icons/BackIcon.png")
        HeaderBarButton(onClick = { setCurrentPage(Pages.Settings)}, filePath = "Icons/GearIcon.png")
    }
}

enum class Pages {
    BodyRegions, ScanType, Scan, Settings
}

@Preview
@Composable
fun DevelopmentScreen() {

    var currentPage by remember { mutableStateOf(Pages.BodyRegions) }
    val setCurrentPage = { page: Pages -> currentPage = page}

    var currentLanguage by remember { mutableStateOf(Language.English) }
    val setLanguage = { language: Language -> currentLanguage = language }

    HeaderBar(setCurrentPage = setCurrentPage)

    // include any modifier that applies to any page here
    val defaultModifier = Modifier.padding(horizontal = Theme.StandardPageMargin)

    when (currentPage) {
        Pages.BodyRegions -> {
            BodyRegionsPage(currentLanguage, defaultModifier)
        }
        Pages.ScanType -> {

        }
        Pages.Scan -> {

        }
        Pages.Settings -> {
            SettingsPage(currentLanguage, setLanguage, defaultModifier)
        }
    }
}