package com.google.aiedge.examples.imageclassification

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ImageFormat
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.content.Context
import android.graphics.YuvImage
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.camera.core.ImageProxy
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import kotlin.math.max
import kotlin.math.min

private val BBOX_SIZE_PERCENT_THRESH = 0.25f

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val viewModel: MainViewModel by viewModels { MainViewModel.getFactory(this) }

        setContent {

            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val uiStateQa by viewModel.uiStateQa.collectAsStateWithLifecycle()

            fun onImageProxyAnalyzed(imageProxy: ImageProxy, context: Context, scanType: String) {

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

            LaunchedEffect(uiState.errorMessage) {
                if (uiState.errorMessage != null) {
                    Toast.makeText(
                        this@MainActivity, "${uiState.errorMessage}", Toast.LENGTH_SHORT
                    ).show()
                    viewModel.errorMessageShown()
                }
            }

//            Framing(viewModel, uiState) {
                MainContent(
                    onImageProxyAnalyzed = ::onImageProxyAnalyzed,
                    mainViewModel = viewModel
                )
//            }
        }
    }
}

fun applyMaskToImage(image: ImageProxy, mask: Bitmap): ImageProxy {
    // Convert ImageProxy to Bitmap
    val imageBitmap = imageProxyToBitmap(image)

    // Ensure mask size matches the image
    val resizedMask = Bitmap.createScaledBitmap(mask, imageBitmap.width, imageBitmap.height, false)

    // Apply the mask
    val maskedBitmap = Bitmap.createBitmap(imageBitmap.width, imageBitmap.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(maskedBitmap)
    val paint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN) }

    canvas.drawBitmap(imageBitmap, 0f, 0f, null) // Draw the original image
    canvas.drawBitmap(resizedMask, 0f, 0f, paint) // Apply the mask

    // Convert Bitmap back to ImageProxy
    return bitmapToImageProxy(maskedBitmap, image)
}

// Convert ImageProxy to Bitmap (Handles YUV_420_888)
private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
    val yBuffer = image.planes[0].buffer // Y
    val uBuffer = image.planes[1].buffer // U
    val vBuffer = image.planes[2].buffer // V

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 100, out)

    val jpegArray = out.toByteArray()
    return BitmapFactory.decodeByteArray(jpegArray, 0, jpegArray.size)!!
}

// Convert Bitmap back to ImageProxy
private fun bitmapToImageProxy(bitmap: Bitmap, originalImageProxy: ImageProxy): ImageProxy {
    val yuvBytes = bitmapToYuv(bitmap)

    val yuvImage = YuvImage(yuvBytes, ImageFormat.NV21, bitmap.width, bitmap.height, null)
    val outputStream = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, bitmap.width, bitmap.height), 100, outputStream)

    val byteArray = outputStream.toByteArray()
    val decodedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

    // We need a valid ImageProxy, so return the original as a placeholder
    return originalImageProxy
}

// Convert Bitmap to YUV format for CameraX compatibility
private fun bitmapToYuv(bitmap: Bitmap): ByteArray {
    val argb8888Buffer = ByteBuffer.allocate(bitmap.byteCount)
    bitmap.copyPixelsToBuffer(argb8888Buffer)
    return argb8888Buffer.array()
}
