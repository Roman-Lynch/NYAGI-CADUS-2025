package com.google.aiedge.examples.imageclassification.pages

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.*
import com.google.aiedge.examples.imageclassification.MainViewModel
import androidx.compose.ui.platform.LocalContext
import com.google.aiedge.examples.imageclassification.UiState
import com.google.aiedge.examples.imageclassification.UiStateQa
import com.google.aiedge.examples.imageclassification.cameraComponents.CameraPermissionsAlert
import com.google.aiedge.examples.imageclassification.cameraComponents.CameraPreview
import com.google.aiedge.examples.imageclassification.cameraComponents.ColoredCameraBorder
import com.google.aiedge.examples.imageclassification.cameraComponents.RotatePhonePopup
import com.google.aiedge.examples.imageclassification.language.GalleryText
import com.google.aiedge.examples.imageclassification.language.Language
import com.google.aiedge.examples.imageclassification.navigation.HeaderBar
import kotlin.math.absoluteValue
import android.graphics.ImageFormat
import android.graphics.Rect
import android.media.Image
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageInfo
import androidx.compose.material.Text
import androidx.compose.material.TextField
import com.google.aiedge.examples.imageclassification.language.LanguageSettingsGateway
import com.google.aiedge.examples.imageclassification.navigation.Pages
import com.google.aiedge.examples.imageclassification.onImageProxyAnalyzed
import java.nio.ByteBuffer

class BreastCameraActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: MainViewModel by viewModels{ MainViewModel.getFactory(this) }

        setContent {
            BreastCameraPage(
                uiState = viewModel.uiState.value,
                uiStateQa = viewModel.uiStateQa.value,
                currentLanguage = LanguageSettingsGateway(LocalContext.current).getSavedLanguage(),
                modifier = Modifier.fillMaxSize(),
                onImageAnalyzed = { imageProxy, context, scanType -> onImageProxyAnalyzed(
                    imageProxy,
                    context,
                    scanType,
                    viewModel,
                    viewModel.uiStateQa.value
                ) },
                viewModel = viewModel,
            )
        }
    }
}
private const val BBOX_SIZE_PERCENT_THRESH = 0.25f

@OptIn(ExperimentalGetImage::class)
@Composable
fun BreastCameraPage(
    uiState: UiState,
    uiStateQa: UiStateQa,
    currentLanguage: Language,
    modifier: Modifier = Modifier,
    onImageAnalyzed: (ImageProxy, Context, String) -> Unit,
    viewModel: MainViewModel
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
        modifier = modifier.fillMaxSize(),
    ) {
        CameraPreview(onImageAnalyzed = ::androidOnImageAnalyzed)
        val categories = uiState.categories
        val qa_box = uiStateQa.QaBox

        var box = false
        var boundingbox = FloatArray(0)
        var maskBool = false
        var mask = Bitmap.createBitmap(320, 320, Bitmap.Config.ARGB_8888)

        // Default screen dimensions (use context to get fallback values if qa_box is empty)
        var screenHeight = 0
        var screenWidth = 0

        // Get the screen dimensions from context as fallback
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        screenHeight = displayMetrics.heightPixels
        screenWidth = displayMetrics.widthPixels

        // Track if we have a valid bounding box from the current frame
        var currentFrameHasValidBox = false

        // Process QA Box Response
        if (qa_box.isNotEmpty()) {
            Log.d("QA BOX", "QA BOX output: [${qa_box[0].x_1}, ${qa_box[0].x_2}, ${qa_box[0].y_1}, ${qa_box[0].y_2}]")
            box = true
            boundingbox = listOf(qa_box[0].x_1, qa_box[0].y_1, qa_box[0].x_2, qa_box[0].y_2).toFloatArray()
            maskBool = true
            mask = qa_box[0].mask

            // Update screen dimensions from qa_box only if it's not empty
            screenHeight = qa_box[0].screenHeight
            screenWidth = qa_box[0].screenWidth

            // Check if current box is big enough to be valid
            val width = (qa_box[0].x_2 - qa_box[0].x_1).absoluteValue
            val height = (qa_box[0].y_2 - qa_box[0].y_1).absoluteValue
            val bboxArea = width * height
            val screenArea = screenWidth * screenHeight
            val bbox_area = (bboxArea.toFloat() / screenArea.toFloat())

            // Set threshold based on your requirements
            currentFrameHasValidBox = bbox_area >= BBOX_SIZE_PERCENT_THRESH

            if (!currentFrameHasValidBox) {
                Log.d("QA BOX", "Box too small for classification")
            }
        } else {
            Log.d("QA BOX", "QA BOX output is empty")
            // No box in this frame
            currentFrameHasValidBox = false
        }

        // Only use classification results if we have a valid box in the current frame
        if (currentFrameHasValidBox && !categories.isEmpty()) {
            var highestCategory = "benign"
            var highestScore = 0.0f
            for (category in categories) {
                if (category.score > highestScore) {
                    highestCategory = category.label
                    highestScore = category.score
                }
            }

            if (highestScore > 0.0f) {
                if (highestCategory == "benign") {
                    ColoredCameraBorder(
                        Color.Yellow,
                        bbox = boundingbox,
                        maskExists = maskBool,
                        mask = mask,
                        confidence = highestScore,
                        screenHeight = screenHeight,
                        screenWidth = screenWidth,
                        classification = true
                    )
                } else if (highestCategory == "malignant") {
                    ColoredCameraBorder(
                        Color.Red,
                        bbox = boundingbox,
                        maskExists = maskBool,
                        mask = mask,
                        confidence = highestScore,
                        screenHeight = screenHeight,
                        screenWidth = screenWidth,
                        classification = true
                    )
                } else {
                    ColoredCameraBorder(
                        Color.Green,
                        bbox = boundingbox,
                        maskExists = maskBool,
                        mask = mask,
                        confidence = highestScore,
                        screenHeight = screenHeight,
                        screenWidth = screenWidth,
                        classification = true
                    )
                }
            } else {
                // Categories exist but scores are zero - draw gray border
                ColoredCameraBorder(
                    Color.Gray,
                    bbox = boundingbox,
                    maskExists = maskBool,
                    mask = mask,
                    confidence = 0f,
                    screenHeight = screenHeight,
                    screenWidth = screenWidth,
                    classification = false
                )
            }
        } else {
            // Either no valid box or no categories - draw gray border
            ColoredCameraBorder(
                Color.Gray,
                bbox = boundingbox,
                maskExists = maskBool,
                mask = mask,
                confidence = 0f,
                screenHeight = screenHeight,
                screenWidth = screenWidth,
                classification = false
            )
        }

        val configuration = LocalConfiguration.current
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            RotatePhonePopup("screen-rotate", currentLanguage, modifier = Modifier.align(Alignment.Center))
        }

//        val context = LocalContext.current
//        LaunchedEffect(Unit) {
//            val intent = Intent(context, com.google.aiedge.examples.imageclassification.pages.BreastCameraActivity::class.java)
//            context.startActivity(intent)
//        }

        val onClickArrow = {
            val intent = Intent(context, com.google.aiedge.examples.imageclassification.MainActivity::class.java)
            context.startActivity(intent)
        }
        val onClickSettings = {
            viewModel.pushPage(Pages.Settings)
        }

        CameraPermissionsAlert(uiState, currentLanguage)
        HeaderBar(currentLanguage, viewModel, Color.DarkGray.copy(alpha = 0.75f), onClickArrow, onClickSettings)
        Log.d("breast123", viewModel.getCurrentPage().toString())
    }

    fun applyBitmapToImageProxy(bitmap: Bitmap, imageProxy: ImageProxy): ImageProxy {
        // Implementation remains the same
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