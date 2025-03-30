package com.google.aiedge.examples.imageclassification.pages

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import com.google.aiedge.examples.imageclassification.UiState
import com.google.aiedge.examples.imageclassification.UiStateQa
import com.google.aiedge.examples.imageclassification.cameraComponents.CameraPermissionsAlert
import com.google.aiedge.examples.imageclassification.cameraComponents.CameraPreview
import com.google.aiedge.examples.imageclassification.cameraComponents.ColoredCameraBorder
import com.google.aiedge.examples.imageclassification.cameraComponents.RotatePhonePopup
import com.google.aiedge.examples.imageclassification.language.GalleryText
import com.google.aiedge.examples.imageclassification.language.Language
import kotlin.math.absoluteValue

@Composable
fun BreastCameraPage(
    uiState: UiState,
    uiStateQa: UiStateQa,
    currentLanguage: Language,
    modifier: Modifier = Modifier,
    onImageAnalyzed: (ImageProxy, Context, String) -> Unit,
) {

    CameraPermissionsAlert(uiState, currentLanguage)

    val context = LocalContext.current
    fun androidOnImageAnalyzed(imageProxy: ImageProxy) {
        onImageAnalyzed(
            imageProxy,
            context,
            GalleryText.getGalleryBreastCancerUltrasoundScanDescription(context, currentLanguage)
        )
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CameraPreview(onImageAnalyzed = ::androidOnImageAnalyzed)
        val categories = uiState.categories
        val qa_box = uiStateQa.QaBox

        var box = false
        var width = 0f
        var height = 0f
        var boundingbox = FloatArray(0)

        // LOG QA Box Response
        if (!qa_box.isEmpty()) {
            Log.d("QA BOX", "QA BOX output: [${qa_box[0].x_1}, ${qa_box[0].x_2}, ${qa_box[0].y_1}, ${qa_box[0].y_2}]")
            width = (qa_box[0].x_2 - qa_box[0].x_1).absoluteValue
            height = (qa_box[0].y_2 - qa_box[0].y_1).absoluteValue
            box = true
            boundingbox = listOf(qa_box[0].x_1, qa_box[0].y_1, qa_box[0].x_2, qa_box[0].y_2).toFloatArray()
        } else {
            Log.d("QA BOX", "QA BOX output is empty")
        }

        var highestCategory = "benign"
        var highestScore = 0.0f
        for (category in categories) {
            if (category.score > highestScore) {
                highestCategory = category.label
                highestScore = category.score
            }
        }
        if (highestScore > .0f) { // change when on phone
            if (highestCategory == "benign" && box){
                ColoredCameraBorder(Color.Green, canvasWidth = width, canvasHeight = height, bbox = boundingbox)
            }
            if (highestCategory == "malignant" && box) {
                ColoredCameraBorder(Color.Red, canvasWidth = width, canvasHeight = height, bbox = boundingbox)
            }
        }
        val configuration = LocalConfiguration.current
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            RotatePhonePopup("screen-rotate", currentLanguage)
        }
    }
}