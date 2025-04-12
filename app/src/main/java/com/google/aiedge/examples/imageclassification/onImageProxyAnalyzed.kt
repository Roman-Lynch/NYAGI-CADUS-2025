package com.google.aiedge.examples.imageclassification

import android.content.Context
import android.util.Log
import androidx.camera.core.ImageProxy
import kotlin.math.max
import kotlin.math.min
import com.google.aiedge.examples.imageclassification.MainViewModel
import com.google.aiedge.examples.imageclassification.UiStateQa

private const val BBOX_SIZE_PERCENT_THRESH = 0.25f

fun onImageProxyAnalyzed(
    imageProxy: ImageProxy,
    context: Context,
    scanType: String,
    viewModel: MainViewModel,
    uiStateQa: UiStateQa
) {

    viewModel.run_QA(imageProxy)
    if (uiStateQa.QaBox.isNotEmpty()) {
        val bbox = uiStateQa.QaBox

        val x1 = min(bbox[0].x_1, bbox[0].x_2)
        val y1 = min(bbox[0].y_1, bbox[0].y_2)
        val x2 = max(bbox[0].x_1, bbox[0].x_2)
        val y2 = max(bbox[0].y_1, bbox[0].y_2)

        // Calculate width and height for the bounding box
        val width = x2 - x1
        val height = y2 - y1

        // Calculate bounding box area and screen area
        val bboxArea = width * height
        val screenArea = bbox[0].screenWidth * bbox[0].screenHeight
        val bboxPercentage = (bboxArea.toFloat() / screenArea.toFloat())

        // ONLY run classification model if the bboxPercentage > BBOX_SIZE_PERCENT_THRESH
        if (bboxPercentage >= BBOX_SIZE_PERCENT_THRESH) {
            Log.d("MainActivity", "Bounding box percentage is ${bboxPercentage} and is large enough to run classification model. Continuing...")
            // APPLY mask to image before processing if there's a mask to apply
            if (uiStateQa.QaBox[0].hasMask && uiStateQa.QaBox[0].mask != null && bboxPercentage > BBOX_SIZE_PERCENT_THRESH) {
                try {
                    viewModel.classify(
                        imageProxy =imageProxy,
                        context =context,
                        scanType =scanType,
                        mask =uiStateQa.QaBox[0].mask
                    )
                    Log.e("MainActivity", "Mask applied to image")
                } catch (e: Exception) {
                    Log.e(
                        "MainActivity",
                        "Error applying mask: ${e.message}",
                        e
                    )
                    viewModel.classify(
                        imageProxy =imageProxy,
                        context =context,
                        scanType =scanType,
                        mask = null
                    )  // Fallback to original image
                }
            } else {
                Log.e(
                    "MainActivity",
                    "No mask found. Running model on unmasked image"
                )
                viewModel.classify(
                    imageProxy =imageProxy,
                    context =context,
                    scanType =scanType,
                    mask = null)
            }
        } else {
            Log.d("MainActivity", "Bounding box percentage is ${bboxPercentage} and is too small to run classification model. Skipping...")
        }
    }
}