package com.google.aiedge.examples.imageclassification.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.aiedge.examples.imageclassification.pages.BodyRegionsPage

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
fun HeaderBarButton(modifier: Modifier = Modifier, filePath: String = "Icons/GearIconWhite.png") {

    var showAlert by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .aspectRatio(1.0f)
            .clip(CircleShape)
            .clickable(onClick = { showAlert = true })
            .background(Brush.linearGradient(
                colors = listOf(Theme.NyagiPurple, Theme.NyagiDarkPurple)
            ))
            .border(2.dp, Theme.Black, CircleShape)
    ) {
        AsyncImage(
            model = "file:///android_asset/${filePath}",
            contentDescription = null,
            modifier = Modifier
                .padding(14.dp)
                .fillMaxSize()
        )
    }

    if (showAlert) {
        DefaultAlert({ showAlert = false })
    }

}

@Preview
@Composable
fun HeaderBar() {

    var textHeight by remember { mutableStateOf(0) }
    val density = LocalDensity.current.density

    Box(
        modifier = Modifier
            .background(Theme.NyagiTeal)
            .fillMaxWidth()
            .height(80.dp)
            .padding(10.dp)
            .onGloballyPositioned { coordinates ->
                // Store the height of the Box
                textHeight = coordinates.size.height
            },
    ) {

        Row() {
            HeaderBarButton(filePath = "Icons/BackIconWhite.png")
            Spacer(Modifier.width(5.dp))
            Text(
                text = "NYAGI",
                color = Theme.Black,
                style = TextStyle(
                    fontSize = (textHeight / density).sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,

                    ),
            )
            Spacer(modifier = Modifier.weight(1f))
            HeaderBarButton()
        }
    }
}

enum class Pages {
    BodyRegions, ScanType, Scan, Settings
}

@Preview
@Composable
fun DevelopmentScreen() {
    val currentPage by remember { mutableStateOf(Pages.BodyRegions) }

    HeaderBar()

    when (currentPage) {
        Pages.BodyRegions -> {
            BodyRegionsPage()
        }
        Pages.ScanType -> {

        }
        Pages.Scan -> {

        }
        Pages.Settings -> {

        }
    }
}