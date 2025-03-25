package com.google.aiedge.examples.imageclassification

import androidx.compose.foundation.layout.Column
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.google.aiedge.examples.imageclassification.legacy.DebugPanel
import com.google.aiedge.examples.imageclassification.view.ApplicationTheme

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun Framing(
    viewModel: MainViewModel,
    uiState: UiState,
    content: @Composable () -> Unit
) {

    ApplicationTheme {
        BottomSheetScaffold(sheetPeekHeight = (90 + 20 * uiState.categories.size).dp,
            sheetContent = {
                DebugPanel(uiState = uiState, onModelSelected = {
                    viewModel.setModel(it)
                }, onDelegateSelected = {
                    viewModel.setDelegate(it)
                }, onThresholdSet = {
                    viewModel.setThreshold(it)
                })
            },
            floatingActionButton = {
            }) {
            Column {
                content()
            }
        }
    }
}