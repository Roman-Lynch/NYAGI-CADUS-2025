package com.google.aiedge.examples.imageclassification.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.aiedge.examples.imageclassification.view.Pages
import com.google.aiedge.examples.imageclassification.view.Theme

@Composable
fun HeaderBar(navigationStack: NavigationStack<Pages>) {
    Row(
        modifier = Modifier
            .background(Theme.Teal)
            .fillMaxWidth()
            .height(80.dp)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        HeaderBarButton(filePath = "Icons/BackIcon.png", onClick = {navigationStack.pop()})
        HeaderBarButton(filePath = "Icons/GearIcon.png", onClick = { navigationStack.push(Pages.Settings) })
    }
}