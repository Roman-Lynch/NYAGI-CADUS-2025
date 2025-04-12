/*
 * Copyright 2024 The Google AI Edge Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.aiedge.examples.imageclassification

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.aiedge.examples.imageclassification.navigation.NavigationStack
import com.google.aiedge.examples.imageclassification.navigation.Pages
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(private val imageClassificationHelper: ImageClassificationHelper,
                    private val context: Context) :
    ViewModel() {
    companion object {
        fun getFactory(context: Context) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val imageClassificationHelper = ImageClassificationHelper(context)
                return MainViewModel(imageClassificationHelper, context) as T
            }
        }
    }

    private val _rerenderCounter = MutableStateFlow(0) // Initial value is 0
    val rerenderCounter: StateFlow<Int> get() = _rerenderCounter

    private fun rerender() {
        _rerenderCounter.value += 1
    }

    private var classificationJob: Job? = null

    private val setting = MutableStateFlow(Setting())
        .apply {
            viewModelScope.launch {
                collect {
                    imageClassificationHelper.setOptions(
                        ImageClassificationHelper.Options(
                            model = it.model,
                            delegate = it.delegate,
                            resultCount = it.resultCount,
                            probabilityThreshold = it.threshold
                        )
                    )
                    imageClassificationHelper.initClassifier()
                    imageClassificationHelper.initQaClassifier()
                }
            }
        }

    private val errorMessage = MutableStateFlow<Throwable?>(null).also {
        viewModelScope.launch {
            imageClassificationHelper.error.collect(it)
        }
    }

    private val _navigationStack = MutableLiveData<NavigationStack>()
    val navigationStack: LiveData<NavigationStack> get() = _navigationStack

    val uiState: StateFlow<UiState> = combine(
        imageClassificationHelper.classification
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                ImageClassificationHelper.ClassificationResult(emptyList(), 0L)
            ),
        setting.filterNotNull(),
        errorMessage,
    ) { result, setting, error ->
        UiState(
            inferenceTime = result.inferenceTime,
            categories = result.categories,
            setting = setting,
            errorMessage = error?.message
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())

    /* RUN QaModel.tflite
        The same as uiState but for YOLO 11 Seg Model */
    val uiStateQa: StateFlow<UiStateQa> = combine(
        imageClassificationHelper.QaClassification
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                ImageClassificationHelper.QaResults(emptyList(), 0L)
            ),
        setting.filterNotNull(),
        errorMessage,
    ) { result, setting, error ->
        UiStateQa(
            QaBox = result.box,
            setting = setting,
            errorMessage = error?.message
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiStateQa())

    init {
        val activityName = context::class.java.simpleName

        when (activityName) {
            "Application" -> {
                _navigationStack.value = NavigationStack(Pages.BodyRegions)
            }
            "BreastCameraActivity" -> {
                _navigationStack.value = NavigationStack(Pages.Scan)
            }
        }
    }

    /** Start classify an image.
     *  @param imageProxy contain `imageBitMap` and imageInfo as `image rotation degrees`.
     *
     */
    fun classify(imageProxy: ImageProxy, context: Context, scanType: String, mask: Bitmap?) {
        classificationJob = viewModelScope.launch {
            imageClassificationHelper.classify(
                bitmap = imageProxy.toBitmap(),
                rotationDegrees = imageProxy.imageInfo.rotationDegrees,
                imageProxy = imageProxy,
                context = context,
                mask = mask,
                scanType = scanType
            )
            imageProxy.close()
        }
    }

    fun run_QA(imageProxy: ImageProxy) {
        classificationJob = viewModelScope.launch {
            imageClassificationHelper.run_QA(
                imageProxy.toBitmap(),
                imageProxy.imageInfo.rotationDegrees,
            )
            imageProxy.close()
        }
    }

    /** Stop current classification */
    fun stopClassify() {
        classificationJob?.cancel()
    }

    /** Set [ImageClassificationHelper.Delegate] (CPU/NNAPI) for ImageSegmentationHelper*/
    fun setDelegate(delegate: ImageClassificationHelper.Delegate) {
        viewModelScope.launch {
            setting.update { it.copy(delegate = delegate) }
        }
    }

    /** Set [ImageClassificationHelper.Model] for ImageSegmentationHelper*/
    fun setModel(model: ImageClassificationHelper.Model) {
        viewModelScope.launch {
            setting.update { it.copy(model = model) }
        }
    }

    /** Set Number of output classes of the [ImageClassificationHelper.Model].  */
    fun setNumberOfResult(numResult: Int) {
        viewModelScope.launch {
            setting.update { it.copy(resultCount = numResult) }
        }
    }

    /** Set the threshold so the label can display score */
    fun setThreshold(threshold: Float) {
        viewModelScope.launch {
            setting.update { it.copy(threshold = threshold) }
        }
    }

    /** Clear error message after it has been consumed*/
    fun errorMessageShown() {
        errorMessage.update { null }
    }

    fun pushPage(page: Pages) {

        Log.d("MainActivity", "Attempted to navigate forward")

        _navigationStack.value?.push(page)
        _navigationStack.value = _navigationStack.value
        rerender()
    }

    fun popPage() {

        Log.d("MainActivity", "Attempted to navigate backward")

        _navigationStack.value?.pop()
        _navigationStack.value = _navigationStack.value
        rerender()
    }

    fun isStackEmpty(): Boolean {
        return _navigationStack.value?.isEmpty() ?: true
    }

    fun getCurrentPage(): Pages {
        return _navigationStack.value!!.getCurrentPage()
    }
}