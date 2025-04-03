package com.google.aiedge.examples.imageclassification.pages

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
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
import android.graphics.ImageFormat
import android.graphics.Rect
import android.media.Image
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageInfo
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

@OptIn(ExperimentalGetImage::class)
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
        var boundingbox = FloatArray(0)
        var maskBool = false
        var mask = Bitmap.createBitmap(320, 320, Bitmap.Config.ARGB_8888)

        // LOG QA Box Response
        if (!qa_box.isEmpty()) {
            Log.d("QA BOX", "QA BOX output: [${qa_box[0].x_1}, ${qa_box[0].x_2}, ${qa_box[0].y_1}, ${qa_box[0].y_2}]")
            box = true
            boundingbox = listOf(qa_box[0].x_1, qa_box[0].y_1, qa_box[0].x_2, qa_box[0].y_2).toFloatArray()
            maskBool = true
            mask = qa_box[0].mask
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
                ColoredCameraBorder(Color.Green, bbox = boundingbox, maskExists = maskBool, mask = mask, confidence = highestScore, screenHeight = qa_box[0].screenHeight, screenWidth = qa_box[0].screenWidth)
            }
            if (highestCategory == "malignant" && box) {
                ColoredCameraBorder(Color.Red, bbox = boundingbox, maskExists = maskBool, mask = mask, confidence = highestScore, screenHeight = qa_box[0].screenHeight, screenWidth = qa_box[0].screenWidth)
            }
        }
        val configuration = LocalConfiguration.current
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            RotatePhonePopup("screen-rotate", currentLanguage)
        }
    }

    fun applyBitmapToImageProxy(bitmap: Bitmap, imageProxy: ImageProxy): ImageProxy {
        val yuvImage = convertBitmapToYuv(bitmap)
        val planes = arrayOfNulls<ImageProxy.PlaneProxy>(3)
        val width = bitmap.width
        val height = bitmap.height
        val rowStride = width
        val pixelStride = 1

        planes[0] = YuvPlaneProxy(yuvImage.y, rowStride, pixelStride)
        planes[1] = YuvPlaneProxy(yuvImage.u, rowStride, pixelStride)
        planes[2] = YuvPlaneProxy(yuvImage.v, rowStride, pixelStride)

        return object : ImageProxy {
            override fun getWidth(): Int = width
            override fun getHeight(): Int = height
            override fun getFormat(): Int = ImageFormat.YUV_420_888
            override fun getPlanes(): Array<ImageProxy.PlaneProxy> = planes.filterNotNull().toTypedArray()
            override fun getImageInfo(): ImageInfo {
                TODO("Not yet implemented")
            }

            override fun getCropRect(): Rect = Rect(0, 0, width, height)
            override fun setCropRect(rect: Rect?) {}
            override fun close() {
                imageProxy.close()
            }

            override fun getImage(): Image? {
                return null
            }
        }
    }
}

private fun convertBitmapToYuv(bitmap: Bitmap): YuvData {
    val width = bitmap.width
    val height = bitmap.height
    val argb = IntArray(width * height)
    bitmap.getPixels(argb, 0, width, 0, 0, width, height)
    val y = ByteArray(width * height)
    val u = ByteArray(width * height / 4)
    val v = ByteArray(width * height / 4)
    var yIndex = 0
    var uvIndex = 0
    for (j in 0 until height) {
        for (i in 0 until width) {
            val pixel = argb[j * width + i]
            val r = pixel shr 16 and 0xff
            val g = pixel shr 8 and 0xff
            val b = pixel and 0xff
            val yValue = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
            val uValue = (-0.169 * r - 0.331 * g + 0.5 * b + 128).toInt()
            val vValue = (0.5 * r - 0.419 * g - 0.081 * b + 128).toInt()
            y[yIndex++] = yValue.toByte()
            if (j % 2 == 0 && i % 2 == 0) {
                u[uvIndex] = uValue.toByte()
                v[uvIndex] = vValue.toByte()
                uvIndex++
            }
        }
    }
    return YuvData(y, u, v)
}

private data class YuvData(val y: ByteArray, val u: ByteArray, val v: ByteArray)

private class YuvPlaneProxy(private val data: ByteArray, private val rowStride: Int, private val pixelStride: Int) :
    ImageProxy.PlaneProxy {
    override fun getRowStride(): Int = rowStride
    override fun getPixelStride(): Int = pixelStride
    override fun getBuffer(): ByteBuffer = ByteBuffer.wrap(data)
}