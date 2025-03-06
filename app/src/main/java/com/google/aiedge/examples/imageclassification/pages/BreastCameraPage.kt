package com.google.aiedge.examples.imageclassification.pages

import android.graphics.Bitmap
import android.net.Uri
import android.content.res.Configuration

import androidx.camera.core.ImageProxy
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.material.Text
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.graphicsLayer

import com.google.aiedge.examples.imageclassification.UiState
import com.google.aiedge.examples.imageclassification.MainViewModel
import com.google.aiedge.examples.imageclassification.MainActivity
import com.google.aiedge.examples.imageclassification.language.Language
import com.google.aiedge.examples.imageclassification.view.CameraScreen

@Composable
fun orientation(
    modifier: Modifier
) {
    Box(
        modifier = Modifier
            .height(25.dp)
            .width(25.dp)
            .padding(25.dp)
            .graphicsLayer {alpha = 0.5f},
        contentAlignment = Alignment.Center
    ) {
        Text("Flip screen")
    }
}

@Composable
fun BreastCameraPage(
    uiState: UiState,
    currentLanguage: Language,
    modifier: Modifier = Modifier,
    onImageProxyAnalyzed: (ImageProxy) -> Unit,
) {
    val configuration = LocalConfiguration.current
    if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
        orientation(modifier = modifier)
    }
    CameraScreen(
        uiState = uiState,
        modifier = modifier,
        onImageAnalyzed = {
            onImageProxyAnalyzed(it)
        }
    )
}