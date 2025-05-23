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

import androidx.compose.runtime.Immutable

/* RUN QaModel.tflite
        The same as UiState but for YOLO 11 Seg Model */
@Immutable
class UiStateQa(
    val inferenceTime: Long = 0L,
    val QaBox: List<ImageClassificationHelper.QaBox> = emptyList(),
    val setting: Setting = Setting(),
    val errorMessage: String? = null,
)

@Immutable
class UiState(
    val inferenceTime: Long = 0L,
    val categories: List<ImageClassificationHelper.Category> = emptyList(),
    val setting: Setting = Setting(),
    val errorMessage: String? = null,
)

@Immutable
data class Setting(
    val model: ImageClassificationHelper.Model = ImageClassificationHelper.DEFAULT_MODEL,
    val delegate: ImageClassificationHelper.Delegate = ImageClassificationHelper.DEFAULT_DELEGATE,
    val resultCount: Int = ImageClassificationHelper.DEFAULT_RESULT_COUNT,
    val threshold: Float = ImageClassificationHelper.DEFAULT_THRESHOLD,
    val threadCount: Int = ImageClassificationHelper.DEFAULT_THREAD_COUNT
)