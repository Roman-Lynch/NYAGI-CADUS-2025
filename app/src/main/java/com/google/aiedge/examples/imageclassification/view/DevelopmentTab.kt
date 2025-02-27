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
import com.google.aiedge.examples.imageclassification.pages.BodyRegionsPage
import com.google.aiedge.examples.imageclassification.pages.SettingsPage

private val horizontalPadding: Dp = 25.dp // standard margins for page

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
fun HeaderBarButton(modifier: Modifier = Modifier, filePath: String = "Icons/GearIcon.png") {

    var showAlert by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .aspectRatio(1.0f)
            .clip(CircleShape)
            .clickable(onClick = { showAlert = true })
    ) {
        AsyncImage(
            model = "file:///android_asset/${filePath}",
            contentDescription = null,
            modifier = Modifier
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
            .background(Theme.NyagiGreen)
            .fillMaxWidth()
            .height(80.dp)
            .padding(10.dp)
            .onGloballyPositioned { coordinates ->
                textHeight = coordinates.size.height
            },
    ) {

        Row() {
            HeaderBarButton(filePath = "Icons/BackIcon.png")
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

@Composable
fun TextHeader(name:String){
    Column(modifier = Modifier
        .padding(horizontal = horizontalPadding)
    ){
        Row(modifier = Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(name,
                modifier=Modifier.padding(vertical=25.dp),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            AsyncImage(
                model = "file:///android_asset/Icons/Help.png",
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
            )

        }
        // purple divider
        Spacer(modifier = Modifier
            .height(3.dp)
            .fillMaxSize()
            .background(Theme.NyagiPurple))
        // bottom whitespace
        Spacer(modifier=Modifier.height(15.dp))
    }
}

enum class Pages {
    BodyRegions, ScanType, Scan, Settings
}

@Preview
@Composable
fun DevelopmentScreen() {
    val currentPage by remember { mutableStateOf(Pages.BodyRegions) }

    // use this to pass any standard styling to subsequent pages
    val standardModifier:Modifier = Modifier.padding(horizontal = horizontalPadding)

    HeaderBar()

    when (currentPage) {
        Pages.BodyRegions -> {
            TextHeader("Body Part Selector")
            BodyRegionsPage()
        }
        Pages.ScanType -> {

        }
        Pages.Scan -> {

        }
        Pages.Settings -> {
            SettingsPage()
        }
    }
}