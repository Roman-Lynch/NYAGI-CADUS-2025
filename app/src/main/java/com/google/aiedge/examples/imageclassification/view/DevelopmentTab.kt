package com.google.aiedge.examples.imageclassification.view

import android.content.Context
import android.util.Log
import android.content.Intent
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
import com.google.aiedge.examples.imageclassification.pages.ImageInfoPage

@Composable
fun DevelopmentScreen(
    onImageProxyAnalyzed: (ImageProxy, Context, String) -> Unit,
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
    val uiStateQa by mainViewModel.uiStateQa.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        currentLanguage = languageSettingsGateway.getSavedLanguage()
        Log.d("LanguageDebug", "Language loaded: ${currentLanguage.name}")
    }

    //HeaderBar(currentLanguage, mainViewModel)
    val onClickArrow = { mainViewModel.popPage() }
    val onClickSettings = { mainViewModel.pushPage(Pages.Settings) }


    val defaultModifier = Modifier.padding(horizontal = Theme.StandardPageMargin)

    val rerenderCounter by mainViewModel.rerenderCounter.collectAsStateWithLifecycle()

    if (rerenderCounter >= 0) when (mainViewModel.getCurrentPage()) {
        Pages.BodyRegions -> {
            HeaderBar(currentLanguage, mainViewModel, Theme.Teal, onClickArrow, onClickSettings)
            BodyRegionsPage(currentLanguage, defaultModifier, mainViewModel)
        }
        Pages.ScanType -> {

        }
        Pages.Scan -> {
            BreastCameraPage(
                uiState = uiState,
                uiStateQa = uiStateQa,
                currentLanguage = currentLanguage,
                modifier = Modifier.fillMaxSize(),
                onImageAnalyzed = onImageProxyAnalyzed,
                viewModel = mainViewModel,
            )
        }
        Pages.Settings -> {
            HeaderBar(currentLanguage, mainViewModel, Theme.Teal, onClickArrow, onClickSettings)
            SettingsPage(currentLanguage, setLanguage, defaultModifier)
        }
        Pages.Gallery -> {
            HeaderBar(currentLanguage, mainViewModel, Theme.Teal, onClickArrow, onClickSettings)
            GalleryPage(currentLanguage, mainViewModel, defaultModifier)
        }
        Pages.ImageInfoPage -> {
            HeaderBar(currentLanguage, mainViewModel, Theme.Teal, onClickArrow, onClickSettings)
//            BELOW: To preview an image page (which can normally only be accessed by clicking on an image in the gallery page,
//            uncomment the following line.
//            This shows an example of how an image page might look.
            ImageInfoPage(mainViewModel)
        }
    }
}