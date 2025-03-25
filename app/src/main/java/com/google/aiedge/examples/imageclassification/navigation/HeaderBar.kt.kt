package com.google.aiedge.examples.imageclassification.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.aiedge.examples.imageclassification.language.HeaderBarText
import com.google.aiedge.examples.imageclassification.language.Language
import com.google.aiedge.examples.imageclassification.language.SettingsPageText
import com.google.aiedge.examples.imageclassification.pages.SettingsPage
import com.google.aiedge.examples.imageclassification.view.Pages
import com.google.aiedge.examples.imageclassification.view.Theme

@Composable
fun HeaderBar(navigationStack: NavigationStack<Pages>, currentLanguage: Language) {
    val context = LocalContext.current

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
            onClick = {navigationStack.pop()},
            semanticsLabel = HeaderBarText.getGoBack(context, currentLanguage)
        )
        HeaderBarButton(
            filePath = "Icons/GearIcon.png",
            onClick = { navigationStack.push(Pages.Settings) },
            semanticsLabel = SettingsPageText.getSettings(context, currentLanguage)
        )
    }
}