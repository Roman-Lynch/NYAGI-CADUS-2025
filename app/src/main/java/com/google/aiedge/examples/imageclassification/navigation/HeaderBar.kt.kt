package com.google.aiedge.examples.imageclassification.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.aiedge.examples.imageclassification.MainViewModel
import com.google.aiedge.examples.imageclassification.language.HeaderBarText
import com.google.aiedge.examples.imageclassification.language.Language
import com.google.aiedge.examples.imageclassification.language.SettingsPageText
import com.google.aiedge.examples.imageclassification.view.Theme

@Composable
fun HeaderBar(currentLanguage: Language, mainViewModel: MainViewModel) {
    Row(
        modifier = Modifier
            .background(Theme.Teal)
            .fillMaxWidth()
            .height(80.dp)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        HeaderBarButton(
            filePath = "Icons/BackIcon.png",
            onClick = { mainViewModel.popPage() },
            semanticsLabel = HeaderBarText.backButtonLabel.get(currentLanguage)
        )
        HeaderBarButton(
            filePath = "Icons/GearIcon.png",
            onClick = { mainViewModel.pushPage(Pages.Settings) },
            semanticsLabel = SettingsPageText.title.get(currentLanguage)
        )
    }
}