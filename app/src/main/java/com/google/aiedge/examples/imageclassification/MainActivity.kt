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

import com.google.aiedge.examples.imageclassification.onImageProxyAnalyzed


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val viewModel: MainViewModel by viewModels { MainViewModel.getFactory(application) }

        setContent {

            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val uiStateQa by viewModel.uiStateQa.collectAsStateWithLifecycle()

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
                    onImageProxyAnalyzed = { imageProxy, context, scanType -> onImageProxyAnalyzed(imageProxy, context, scanType, viewModel, uiStateQa) },
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
