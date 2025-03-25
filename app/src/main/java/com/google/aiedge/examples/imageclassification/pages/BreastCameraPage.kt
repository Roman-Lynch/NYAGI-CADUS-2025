package com.google.aiedge.examples.imageclassification.pages

import android.content.res.Configuration
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.*
import com.google.aiedge.examples.imageclassification.UiState
import com.google.aiedge.examples.imageclassification.cameraComponents.CameraPermissionsAlert
import com.google.aiedge.examples.imageclassification.cameraComponents.CameraPreview
import com.google.aiedge.examples.imageclassification.cameraComponents.ColoredCameraBorder
import com.google.aiedge.examples.imageclassification.cameraComponents.RotatePhonePopup
import com.google.aiedge.examples.imageclassification.language.Language

@Composable
fun BreastCameraPage(
    uiState: UiState,
    currentLanguage: Language,
    modifier: Modifier = Modifier,
    onImageAnalyzed: (ImageProxy) -> Unit,
) {

    CameraPermissionsAlert(uiState, currentLanguage)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CameraPreview(onImageAnalyzed = { imageProxy ->
            onImageAnalyzed(imageProxy)
        })
        val categories = uiState.categories
        var highestCategory = "benign"
        var highestScore = 0.0f
        for (category in categories) {
            if (category.score > highestScore) {
                highestCategory = category.label
                highestScore = category.score
            }
        }
        if (highestScore > .0f) { // change when on phone
            if (highestCategory == "benign") {
                ColoredCameraBorder(Color.Green)
            }
            if (highestCategory == "malignant") {
                ColoredCameraBorder(Color.Red)
            }
        }
        val configuration = LocalConfiguration.current
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            RotatePhonePopup("screen-rotate", currentLanguage)
        }
    }
}