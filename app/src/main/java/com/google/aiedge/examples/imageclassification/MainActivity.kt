package com.google.aiedge.examples.imageclassification

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.camera.core.ImageProxy
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle

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