package com.google.aiedge.examples.imageclassification.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import coil.compose.AsyncImage
import com.google.aiedge.examples.imageclassification.language.HeaderBarText
import com.google.aiedge.examples.imageclassification.language.Language

@Composable
fun HeaderBarButtonVert(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    filePath: String,
    semanticsLabel: String
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .aspectRatio(1.0f)
            .clickable(onClick = onClick)
            .semantics { contentDescription = semanticsLabel },
    ) {
        AsyncImage(
            model = "file:///android_asset/${filePath}",
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

@Composable
fun HeaderBarButtonHor(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    filePath: String,
    semanticsLabel: String
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.0f)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .semantics { contentDescription = semanticsLabel },
    ) {
        AsyncImage(
            model = "file:///android_asset/${filePath}",
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}
