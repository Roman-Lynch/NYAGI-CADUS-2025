package com.google.aiedge.examples.imageclassification.cameraComponents

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.aiedge.examples.imageclassification.NoCameraPermissionsAlert
import com.google.aiedge.examples.imageclassification.UiState
import com.google.aiedge.examples.imageclassification.language.Language

@Composable
fun CameraPermissionsAlert(uiState: UiState,currentLanguage: Language) {

    var showAlert by remember { mutableStateOf(true) }

    val cameraPermissionsRequestLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean -> if (isGranted) showAlert = false }

    if (showAlert) NoCameraPermissionsAlert({showAlert = false}, currentLanguage)

    val context = LocalContext.current
    LaunchedEffect(key1 = uiState.errorMessage) {

        val permission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.CAMERA
        )

        if (permission == PackageManager.PERMISSION_GRANTED) return@LaunchedEffect

        cameraPermissionsRequestLauncher.launch(android.Manifest.permission.CAMERA)
    }
}