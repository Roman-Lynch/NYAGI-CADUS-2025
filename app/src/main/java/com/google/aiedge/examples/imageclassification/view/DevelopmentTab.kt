package com.google.aiedge.examples.imageclassification.view

import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.aiedge.examples.imageclassification.MainViewModel
import com.google.aiedge.examples.imageclassification.language.Language
import com.google.aiedge.examples.imageclassification.language.LanguageSettingsGateway
import com.google.aiedge.examples.imageclassification.navigation.HeaderBar
import com.google.aiedge.examples.imageclassification.navigation.Pages
import com.google.aiedge.examples.imageclassification.pages.BodyRegionsPage
import com.google.aiedge.examples.imageclassification.pages.SettingsPage
import com.google.aiedge.examples.imageclassification.pages.BreastCameraPage
import com.google.aiedge.examples.imageclassification.pages.GalleryPage

@Composable
fun DevelopmentScreen(
    onImageProxyAnalyzed: (ImageProxy) -> Unit,
    mainViewModel: MainViewModel,
) {

    val languageSettingsGateway = LanguageSettingsGateway(LocalContext.current)
    var currentLanguage by remember { mutableStateOf(Language.ENGLISH) }
    val setLanguage: (Language) -> Unit = { language ->
        currentLanguage = language
        languageSettingsGateway.setSavedLanguage(language)
        Log.d("LanguageDebug", "Language set: ${language.name}")
    }

    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        currentLanguage = languageSettingsGateway.getSavedLanguage()
        Log.d("LanguageDebug", "Language loaded: ${currentLanguage.name}")
    }

    HeaderBar(currentLanguage, mainViewModel)

    val defaultModifier = Modifier.padding(horizontal = Theme.StandardPageMargin)

    val rerenderCounter by mainViewModel.rerenderCounter.collectAsStateWithLifecycle()

    if (rerenderCounter >= 0) when (mainViewModel.getCurrentPage()) {
        Pages.BodyRegions -> {
            BodyRegionsPage(currentLanguage, defaultModifier, mainViewModel)
        }
        Pages.ScanType -> {

        }
        Pages.Scan -> {
            BreastCameraPage(
                uiState = uiState,
                currentLanguage = currentLanguage,
                modifier = Modifier.fillMaxWidth(),
                onImageAnalyzed = onImageProxyAnalyzed,
            )
        }
        Pages.Settings -> {
            SettingsPage(currentLanguage, setLanguage, defaultModifier)
        }
        Pages.Gallery -> {
            GalleryPage(currentLanguage)
        }
    }
}