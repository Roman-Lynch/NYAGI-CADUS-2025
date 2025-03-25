package com.google.aiedge.examples.imageclassification

import androidx.camera.core.ImageProxy
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.aiedge.examples.imageclassification.navigation.Pages
import com.google.aiedge.examples.imageclassification.view.DevelopmentScreen


@Composable
fun MainContent(
    modifier: Modifier = Modifier,
    onImageProxyAnalyzed: (ImageProxy) -> Unit,
    mainViewModel: MainViewModel
) {

    Column(modifier) {
        DevelopmentScreen(
            onImageProxyAnalyzed = {
                onImageProxyAnalyzed(it)
            },
            mainViewModel = mainViewModel
        )
    }
}