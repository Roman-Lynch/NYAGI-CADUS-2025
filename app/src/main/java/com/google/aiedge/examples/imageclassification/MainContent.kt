package com.google.aiedge.examples.imageclassification

import android.content.Context
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.aiedge.examples.imageclassification.view.DevelopmentScreen


@Composable
fun MainContent(
    modifier: Modifier = Modifier,
    onImageProxyAnalyzed: (ImageProxy, Context, String) -> Unit,
    mainViewModel: MainViewModel
) {

    Column(modifier) {
        DevelopmentScreen(
            onImageProxyAnalyzed = onImageProxyAnalyzed,
            mainViewModel = mainViewModel
        )
    }
}