package com.google.aiedge.examples.imageclassification.view

import androidx.camera.core.ImageProxy
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.aiedge.examples.imageclassification.UiState
import com.google.aiedge.examples.imageclassification.language.Language
import com.google.aiedge.examples.imageclassification.language.LanguageSettingsGateway
import com.google.aiedge.examples.imageclassification.navigation.HeaderBar
import com.google.aiedge.examples.imageclassification.navigation.NavigationStack
import com.google.aiedge.examples.imageclassification.pages.BodyRegionsPage
import com.google.aiedge.examples.imageclassification.pages.SettingsPage
import com.google.aiedge.examples.imageclassification.pages.BreastCameraPage
import com.google.aiedge.examples.imageclassification.pages.GalleryPage

enum class Pages {
    BodyRegions, ScanType, Scan, Settings, Gallery
}

@Composable
fun DevelopmentScreen(uiState: UiState, onImageProxyAnalyzed: (ImageProxy) -> Unit) {

    var currentPage by remember { mutableStateOf(Pages.BodyRegions) }
    val setCurrentPage = { page: Pages -> currentPage = page}

    val languageSettingsGateway = LanguageSettingsGateway(LocalContext.current)
    var currentLanguage by remember { mutableStateOf(languageSettingsGateway.getSavedLanguage()) }
    val setLanguage = {
        language: Language -> currentLanguage = language
        languageSettingsGateway.setSavedLanguage(language)
    }

    val navigationStack by remember { mutableStateOf(NavigationStack(setCurrentPage, Pages.BodyRegions))}

    HeaderBar(navigationStack, currentLanguage)

    val defaultModifier = Modifier.padding(horizontal = Theme.StandardPageMargin)

    when (currentPage) {
        Pages.BodyRegions -> {
            BodyRegionsPage(currentLanguage, defaultModifier, navigationStack)
        }
        Pages.ScanType -> {

        }
        Pages.Scan -> {
            BreastCameraPage(
                uiState,
                currentLanguage,
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