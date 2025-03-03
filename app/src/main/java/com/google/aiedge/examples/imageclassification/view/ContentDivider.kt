package com.google.aiedge.examples.imageclassification.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ContentDivider() {
    Spacer(modifier = Modifier
        .height(3.dp)
        .fillMaxSize()
        .background(Theme.Purple))
}