package com.google.aiedge.examples.imageclassification.pages

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.content.res.Configuration
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.camera.core.AspectRatio.RATIO_4_3
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis

import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size.Companion.ORIGINAL

import com.google.aiedge.examples.imageclassification.UiState
import com.google.aiedge.examples.imageclassification.MainViewModel
import com.google.aiedge.examples.imageclassification.MainActivity
import com.google.aiedge.examples.imageclassification.language.CameraText
import com.google.aiedge.examples.imageclassification.language.Language
import com.google.aiedge.examples.imageclassification.view.CameraScreen
import com.google.aiedge.examples.imageclassification.view.Theme
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun BreastCameraPage(
    uiState: UiState,
    currentLanguage: Language,
    modifier: Modifier = Modifier,
    onImageAnalyzed: (ImageProxy) -> Unit,
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Do nothing
        } else {
            // Permission Denied
            Toast.makeText(context, "Camera permission is denied", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(key1 = uiState.errorMessage) {
        if (ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Do nothing
        } else {
            launcher.launch(android.Manifest.permission.CAMERA)
        }
    }
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
                dangerWarning(modifier = Modifier, Color.Green)
            }
            if (highestCategory == "malignant") {
                dangerWarning(modifier = Modifier, Color.Red)
            }
        }
        val configuration = LocalConfiguration.current
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            orientation(modifier = Modifier, "screen-rotate", currentLanguage)
        }
    }
}

@Composable
fun orientation(
    modifier: Modifier,
    optionName: String,
    currentLanguage: Language
) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data("file:///android_asset/Icons/${optionName}.png")
            .size(ORIGINAL)
            .build(),
        contentScale = ContentScale.FillBounds
    )
    Box(
        modifier = Modifier
            .height(300.dp)
            .width(300.dp)
            .padding(25.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(Color.Black.copy(alpha = 0.80f), shape = RoundedCornerShape(25.dp))
            .paint(painter, alignment = Alignment.TopCenter, colorFilter = ColorFilter.tint(Color.White)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = CameraText.rotateScreen.get(currentLanguage),
            modifier = Modifier.align(Alignment.BottomCenter).padding(5.dp),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

// adapted from https://stackoverflow.com/questions/74074525/jetpack-compose-how-to-cut-out-card-shape
// and https://blog.jakelee.co.uk/how-to-make-cutouts-in-jetpack-compose-boxes/
@Composable
fun dangerWarning(
    modifier: Modifier,
    color: Color
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
            }
            .drawWithContent {
                drawContent()

                val canvasWidth = size.width
                val canvasHeight = size.height


                val width = canvasWidth * .9f
                val height = canvasHeight * .95f
                drawRect(
                    color = Color(0xFFFFFFFF),
                    topLeft = Offset(x = (canvasWidth - width) / 2, y = (canvasHeight - height) / 2),
                    size = Size(width, height),
                    blendMode = BlendMode.DstOut
                )
            }
            .background(color.copy(alpha = 0.3f))
    ) {

    }
}

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    onImageAnalyzed: (ImageProxy) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture by remember {
        mutableStateOf(ProcessCameraProvider.getInstance(context))
    }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            cameraProviderFuture.get().unbindAll()
        }
    }

    AndroidView(modifier = modifier, factory = {
        val previewView = PreviewView(it).apply {
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }

        val executor = Executors.newSingleThreadExecutor()
        cameraProviderFuture.addListener({
            bindCameraUseCases(
                lifecycleOwner = lifecycleOwner,
                cameraProviderFuture = cameraProviderFuture,
                executor = executor,
                previewView = previewView,
                onImageAnalyzed = onImageAnalyzed
            )
        }, ContextCompat.getMainExecutor(context))

        previewView
    })
}

fun bindCameraUseCases(
    lifecycleOwner: LifecycleOwner,
    cameraProviderFuture: ListenableFuture<ProcessCameraProvider>,
    executor: ExecutorService,
    previewView: PreviewView,
    onImageAnalyzed: (ImageProxy) -> Unit,
) {
    val preview: Preview = Preview.Builder().setTargetAspectRatio(RATIO_4_3).build()

    preview.setSurfaceProvider(previewView.surfaceProvider)

    val cameraSelector: CameraSelector =
        CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
    val imageAnalysis = ImageAnalysis.Builder().setTargetAspectRatio(RATIO_4_3)
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888).build()

    imageAnalysis.setAnalyzer(executor) { imageProxy ->
        onImageAnalyzed(imageProxy)
    }
    val cameraProvider = cameraProviderFuture.get()

    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, imageAnalysis, preview)
}