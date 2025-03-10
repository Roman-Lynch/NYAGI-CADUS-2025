package com.google.aiedge.examples.imageclassification.view

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// currently only used by pre-made stuff; using entirely different colors to make it easier to judge our own UI
@Composable
fun ApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = MaterialTheme.colors.copy(
            primary = Color(0xFF0E0E60),
            secondary = Color.Black,
            onSurface = Color.Black,
        ),
        content = content
    )
}
