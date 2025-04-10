package com.google.aiedge.examples.imageclassification.navigation

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Colors
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.aiedge.examples.imageclassification.MainViewModel
import com.google.aiedge.examples.imageclassification.language.GalleryText
import com.google.aiedge.examples.imageclassification.language.HeaderBarText
import com.google.aiedge.examples.imageclassification.language.Language
import com.google.aiedge.examples.imageclassification.language.SettingsPageText
import com.google.aiedge.examples.imageclassification.view.Theme

@Composable
fun HeaderBar(currentLanguage: Language, mainViewModel: MainViewModel, color: Color) {
    val configuration = LocalConfiguration.current
    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            Column (
                modifier = Modifier
                    .background(color)
                    .height(configuration.screenHeightDp.dp)
                    .width(80.dp)
                    .padding(10.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                HeaderBarButtonHor(
                    filePath = "Icons/BackIcon.png",
                    onClick = { mainViewModel.popPage() },
                    semanticsLabel = HeaderBarText.getGoBack(LocalContext.current, currentLanguage)
                )
                HeaderBarButtonHor(
                    filePath = "Icons/GearIcon.png",
                    onClick = { mainViewModel.pushPage(Pages.Settings) },
                    semanticsLabel = SettingsPageText.getSettings(LocalContext.current, currentLanguage)
                )
            }
        }
        else -> {
            Row(
                modifier = Modifier
                    .background(color)
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(10.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                HeaderBarButtonVert(
                    filePath = "Icons/BackIcon.png",
                    onClick = { mainViewModel.popPage() },
                    semanticsLabel = HeaderBarText.getGoBack(LocalContext.current, currentLanguage)
                )
                HeaderBarButtonVert(
                    filePath = "Icons/GearIcon.png",
                    onClick = { mainViewModel.pushPage(Pages.Settings) },
                    semanticsLabel = SettingsPageText.getSettings(LocalContext.current, currentLanguage)
                )
            }

        }
    }
}