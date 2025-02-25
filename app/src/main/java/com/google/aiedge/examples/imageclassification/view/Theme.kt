package com.google.aiedge.examples.imageclassification.view

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun ApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = MaterialTheme.colors.copy(
            primary = Theme.DarkBlue,
            secondary = Theme.Teal,
            onSurface = Theme.Teal,
        ),
        content = content
    )
}
