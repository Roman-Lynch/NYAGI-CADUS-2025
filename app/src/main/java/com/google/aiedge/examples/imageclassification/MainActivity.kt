package com.google.aiedge.examples.imageclassification

import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Rect
import android.media.Image
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageInfo
import androidx.camera.core.ImageProxy
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.nio.ByteBuffer

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val viewModel: MainViewModel by viewModels { MainViewModel.getFactory(this) }

        setContent {

            fun onImageProxyAnalyzed(imageProxy: ImageProxy, context: Context, scanType: String) {
                viewModel.classify(imageProxy, context, scanType)
                viewModel.run_QA(imageProxy)
            }

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
                    onImageProxyAnalyzed = ::onImageProxyAnalyzed,
                    mainViewModel = viewModel
                )
//            }
        }
    }
}

@OptIn(ExperimentalGetImage::class)
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
        override fun getPlanes(): Array<ImageProxy.PlaneProxy> =
            planes.filterNotNull().toTypedArray()

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

fun convertBitmapToYuv(bitmap: Bitmap): YuvData {
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

data class YuvData(val y: ByteArray, val u: ByteArray, val v: ByteArray)

class YuvPlaneProxy(private val data: ByteArray, private val rowStride: Int, private val pixelStride: Int) :
    ImageProxy.PlaneProxy {
    override fun getRowStride(): Int = rowStride
    override fun getPixelStride(): Int = pixelStride
    override fun getBuffer(): ByteBuffer = ByteBuffer.wrap(data)
}