package com.google.aiedge.examples.imageclassification.navigation

import android.content.res.Configuration
import android.util.Log
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
fun HeaderBar(
    currentLanguage: Language,
    mainViewModel: MainViewModel,
    color: Color,
    onClickArrow: () -> Unit,
    onClickSettings: () -> Unit,
    ) {
    val configuration = LocalConfiguration.current
    when (configuration.orientation) {
        // DISPLAYS IN HORIZONTAL MODE
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
                    onClick = onClickArrow,
                    semanticsLabel = HeaderBarText.getGoBack(LocalContext.current, currentLanguage)
                )
                // push gallery and settings icons to bottom
                Column(){
                    HeaderBarButtonHor(
                        filePath = "Icons/GalleryIcon.png",
                        onClick = {mainViewModel.pushPage(Pages.Gallery)},
                        semanticsLabel = GalleryText.getGalleryText(LocalContext.current, currentLanguage)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    HeaderBarButtonHor(
                        filePath = "Icons/GearIcon.png",
                        onClick = { mainViewModel.pushPage(Pages.Settings) },
                        semanticsLabel = SettingsPageText.getSettings(LocalContext.current, currentLanguage))
                }
            }
        }
        else -> {
            // DISPLAYS IN VERTICAL MODE
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
                    // onClick = { mainViewModel.popPage() },
                    onClick = onClickArrow,
                    semanticsLabel = HeaderBarText.getGoBack(LocalContext.current, currentLanguage)
                )
                // pushes gallery and settings icons to side
                Row(){
                    HeaderBarButtonVert(
                        filePath = "Icons/GalleryIcon.png",
                        onClick = {mainViewModel.pushPage(Pages.Gallery)},
                        semanticsLabel = GalleryText.getGalleryText(LocalContext.current, currentLanguage)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    HeaderBarButtonVert(
                        filePath = "Icons/GearIcon.png",
                        // onClick = { mainViewModel.pushPage(Pages.Settings); Log.d("gear", "gear has been pressed") },
                        onClick = onClickSettings,
                        semanticsLabel = SettingsPageText.getSettings(LocalContext.current, currentLanguage)
                    )
                }
            }

        }
    }
}