package com.google.aiedge.examples.imageclassification.pages

import android.graphics.Bitmap
import android.net.Uri
import android.content.res.Configuration

import androidx.camera.core.ImageProxy
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.material.Text
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.clip

import com.google.aiedge.examples.imageclassification.UiState
import com.google.aiedge.examples.imageclassification.MainViewModel
import com.google.aiedge.examples.imageclassification.MainActivity
import com.google.aiedge.examples.imageclassification.language.Language
import com.google.aiedge.examples.imageclassification.view.CameraScreen
import com.google.aiedge.examples.imageclassification.view.Theme



@Composable
fun BreastCameraPage(
    uiState: UiState,
    currentLanguage: Language,
    modifier: Modifier = Modifier,
    onImageProxyAnalyzed: (ImageProxy) -> Unit,
) {
    CameraScreen(
        uiState = uiState,
        modifier = modifier,
        onImageAnalyzed = {
            onImageProxyAnalyzed(it)
        }
    )


}