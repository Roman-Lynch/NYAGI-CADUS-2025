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
import android.os.SystemClock
import android.util.Log
import androidx.camera.core.ImageProxy
import com.google.aiedge.examples.imageclassification.data.GalleryImage
import com.google.aiedge.examples.imageclassification.data.GalleryImages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.support.metadata.MetadataExtractor
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ImageClassificationHelper(
    private val context: Context,
    private var options: Options = Options(),
) {
    class Options(
        /** The enum contains the model file name, relative to the assets/ directory */
        var model: Model = DEFAULT_MODEL,
        var QaModel: QaModel = DEFAULT_QA_MODEL,
        /** The delegate for running computationally intensive operations*/
        var delegate: Delegate = DEFAULT_DELEGATE,
        /** Number of output classes of the TFLite model.  */
        var resultCount: Int = DEFAULT_RESULT_COUNT,
        /** Probability value above which a class is labeled as active (i.e., detected) the display.  */
        var probabilityThreshold: Float = DEFAULT_THRESHOLD,
        /** Number of threads to be used for ops that support multi-threading.
         * threadCount>= -1. Setting numThreads to 0 has the effect of disabling multithreading,
         * which is equivalent to setting numThreads to 1. If unspecified, or set to the value -1,
         * the number of threads used will be implementation-defined and platform-dependent.
         * */
        var threadCount: Int = DEFAULT_THREAD_COUNT
    )

    companion object {
        private const val TAG = "ImageClassification"

        val DEFAULT_MODEL = Model.EfficientNet
        val DEFAULT_QA_MODEL = QaModel.QaYOLO
        val DEFAULT_DELEGATE = Delegate.CPU
        const val DEFAULT_RESULT_COUNT = 1

        /* SHOULD PROBABLY BE >0.8 */
        const val DEFAULT_THRESHOLD = 0.0f
        const val DEFAULT_THREAD_COUNT = 2

    }

    /* ONLY ADDED TO GET MODEL WORKING. THIS SHOULD BE INCLUDED in training from now on"*/
    fun softmax(scores: FloatArray): FloatArray {
        val maxScore = scores.maxOrNull() ?: 0f  // Prevents large negative exponentiation
        val expScores = scores.map { Math.exp((it - maxScore).toDouble()) }.toDoubleArray()
        val sumExpScores = expScores.sum()
        return expScores.map { (it / sumExpScores).toFloat() }.toFloatArray()
    }

    /* Used to load labels if the labels aren't properly loaded by labels = getModelMetadata(litertBuffer)*/
    fun loadLabels(context: Context): List<String> {
        val labels = mutableListOf<String>()
        try {
            // Open the labels file from the assets folder
            val inputStream: InputStream = context.assets.open("labels.txt")
            val reader = BufferedReader(InputStreamReader(inputStream))

            // Read each line and add to the list
            reader.useLines { lines ->
                lines.forEach { labels.add(it) }
            }
        } catch (e: Exception) {
            Log.e("ImageClassification", "Error loading labels", e)
        }
        return labels
    }

    /** As the result of sound classification, this value emits map of probabilities */
    val classification: SharedFlow<ClassificationResult>
        get() = _classification
    private val _classification = MutableSharedFlow<ClassificationResult>(
        extraBufferCapacity = 64, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    /* RUN QaModel.tflite
        The same as classification but for YOLO 11 Seg Model */
    val QaClassification: SharedFlow<QaResults>
        get() = _QaClassification
    private val _QaClassification = MutableSharedFlow<QaResults>(
        extraBufferCapacity = 64, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val error: SharedFlow<Throwable?>
        get() = _error
    private val _error = MutableSharedFlow<Throwable?>()

    private var interpreter: Interpreter? = null
    private var qa_interpreter: Interpreter? = null
    private lateinit var labels: List<String>

    /** Init a Interpreter from [Model] with [Delegate]*/
    suspend fun initClassifier() {
        interpreter = try {
            val litertBuffer = FileUtil.loadMappedFile(context, options.model.fileName)
            Log.i(TAG, "Done creating TFLite buffer from ${options.model.fileName}")
            val options = Interpreter.Options().apply {
                numThreads = options.threadCount
                useNNAPI = options.delegate == Delegate.NNAPI
            }
            labels = loadLabels(context)

            /* THIS CURRENTLY DOESN'T WORK*/
            /*labels = getModelMetadata(litertBuffer)*/

            Interpreter(litertBuffer, options)
        } catch (e: Exception) {
            Log.i(TAG, "Create TFLite from ${options.model.fileName} is failed ${e.message}")
            _error.emit(e)
            null
        }
    }

    /* RUN QaModel.tflite
        The same as initClassifier but for YOLO 11 Seg Model */
    suspend fun initQaClassifier() {
        qa_interpreter = try {
            val litertBuffer = FileUtil.loadMappedFile(context, options.QaModel.fileName)
            Log.i(TAG, "Done creating TFLite buffer from ${options.QaModel.fileName}")
            val options = Interpreter.Options().apply {
                numThreads = options.threadCount
                useNNAPI = options.delegate == Delegate.NNAPI
            }
            labels = loadLabels(context)

            /* THIS CURRENTLY DOESN'T WORK*/
            /*labels = getModelMetadata(litertBuffer)*/

            Interpreter(litertBuffer, options)
        } catch (e: Exception) {
            Log.i(TAG, "Create TFLite from ${options.QaModel.fileName} is failed ${e.message}")
            _error.emit(e)
            null
        }
    }

    fun setOptions(options: Options) {
        this.options = options
    }

    /* RUN QaModel.tflite
        The same as classify but for YOLO 11 Seg Model */
    suspend fun run_QA(bitmap: Bitmap, rotationDegrees: Int) {
        try {
            withContext(Dispatchers.IO) {
                if (qa_interpreter == null) return@withContext
                val startTime = SystemClock.uptimeMillis()

                val rotation = -rotationDegrees / 90
                val (_, h, w, _) = qa_interpreter?.getInputTensor(0)?.shape() ?: return@withContext
                val imageProcessor =
                    ImageProcessor.Builder().add(ResizeOp(h, w, ResizeOp.ResizeMethod.BILINEAR))
                        .add(Rot90Op(rotation)).add(NormalizeOp(127.5f, 127.5f)).build()

                // Preprocess the image and convert it into a TensorImage for classification.
                val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))
                val output = runQaWithTFLite(tensorImage)

                val box = output.map {
                    QaBox(x_1 = output[0], x_2 = output[1], y_1 =output[2], y_2 =output[3])
                }.take(options.resultCount)

                val inferenceTime = SystemClock.uptimeMillis() - startTime

                fun logQaResults() {
                    if (box.isNotEmpty()) {
                        Log.i(TAG, "QA Classification Complete complete. Box: ${box}")
                    } else {
                        Log.w(TAG, "Category empty")
                    }
                }
                logQaResults()

                if (isActive) {
                    _QaClassification.emit(QaResults(box, inferenceTime))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "QA classification error occurred: ${e.message}", e)
            _error.emit(e)
        }
    }

    suspend fun classify(imageProxy: ImageProxy, bitmap: Bitmap, rotationDegrees: Int, context: Context, scanType: String)) {
        try {
            withContext(Dispatchers.IO) {
                if (interpreter == null) return@withContext
                val startTime = SystemClock.uptimeMillis()

                val rotation = -rotationDegrees / 90
                val (_, h, w, _) = interpreter?.getInputTensor(0)?.shape() ?: return@withContext
                val imageProcessor =
                    ImageProcessor.Builder().add(ResizeOp(h, w, ResizeOp.ResizeMethod.BILINEAR))
                        .add(Rot90Op(rotation)).add(NormalizeOp(127.5f, 127.5f)).build()

                // Preprocess the image and convert it into a TensorImage for classification.
                val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))
                val output = classifyWithTFLite(tensorImage)

                val outputList = output.map {
                    if (it < options.probabilityThreshold) 0f else it
                }

                val categories = labels.zip(outputList).map {
                    Category(label = it.first, score = it.second)
                }.sortedByDescending { it.score }.take(options.resultCount)

                val inferenceTime = SystemClock.uptimeMillis() - startTime

                fun logClassificationResults() {
                    if (categories.isNotEmpty()) {
                        Log.i(TAG, "Classification complete. Top label: ${categories[0].label}, Score: ${categories[0].score}")
                    } else {
                        Log.w(TAG, "Category empty")
                    }
                }
                logClassificationResults()

                fun saveImageToGallery() {

                    val currentDateTime = LocalDateTime.now()
                    val dateString = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    val timeString = currentDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

                    val galleryImages = GalleryImages(context)
                    val imageMetadata = GalleryImage(
                        dateString = dateString,
                        timeString = timeString,
                        scanTypeString = scanType,
                        label = categories[0].label,
                        confidence = categories[0].score.toDouble(),
                        scanID = UUID.randomUUID(),
                        patientName = "",
                    )
                    galleryImages.addImage(galleryImage = imageMetadata, imageProxy = imageProxy)
                }
                val shouldSaveToGallery = true
                if (shouldSaveToGallery) saveImageToGallery()

                if (isActive) {
                    _classification.emit(ClassificationResult(categories, inferenceTime))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Image classification error occurred: ${e.message}", e)
            _error.emit(e)
        }
    }

    /* RUN QaModel.tflite
        The same as classifyWithTFLite but for YOLO 11 Seg Model */
    private fun runQaWithTFLite(tensorImage: TensorImage): FloatArray {
        val outputShape = qa_interpreter!!.getOutputTensor(0).shape()
        val outputBuffer = FloatBuffer.allocate(outputShape.reduce { acc, i -> acc * i })

        outputBuffer.rewind()
        qa_interpreter?.run(tensorImage.tensorBuffer.buffer, outputBuffer)
        outputBuffer.rewind()
        var output = FloatArray(outputBuffer.capacity())
        outputBuffer.get(output)

        val numClasses = 32 // Update this based on your model
        val bestDetection = YoloSegProcessor.getHighestConfidenceDetection(output, numClasses)

        Log.d("YOLO Output", "First 20 values: ${output.take(20).joinToString()}")
        Log.i(TAG, "YOLO detection output ${bestDetection}")

        if (bestDetection != null) {
            val bbox = bestDetection.bbox // YOLO output is normalized or relative to model input size
            val modelInputSize = 640f // YOLO input size (assumed 640x640)

            val originalHeight = tensorImage.height
            val originalWidth = tensorImage.width


            // Scale bounding box coordinates back to original image size
            val scaledBBox = floatArrayOf(
                bbox[0] * originalWidth,  // x_min
                bbox[1] * originalHeight, // y_min
                bbox[2] * originalWidth,  // x_max
                bbox[3] * originalHeight  // y_max
            )

            return scaledBBox
        } else {
            return FloatArray(0) // Return empty if no detection
        }
    }

    private fun classifyWithTFLite(tensorImage: TensorImage): FloatArray {
        val outputShape = interpreter!!.getOutputTensor(0).shape()
        val outputBuffer = FloatBuffer.allocate(outputShape.reduce { acc, i -> acc * i })

        outputBuffer.rewind()
        interpreter?.run(tensorImage.tensorBuffer.buffer, outputBuffer)
        outputBuffer.rewind()
        val output = FloatArray(outputBuffer.capacity())
        outputBuffer.get(output)
        Log.i(TAG, "Raw output scores: ${softmax(output).joinToString()}")
        Log.i(TAG, "Model input shape: ${interpreter?.getInputTensor(0)?.shape()?.joinToString()}")
        Log.i(TAG, "Model output shape: ${interpreter?.getOutputTensor(0)?.shape()?.joinToString()}")
        Log.i(TAG, "Loaded labels: ${labels.joinToString()}")

        /* HAD TO USE SOFTMAX HERE. SHOULD WORK without softmax in future*/
        return softmax(output)
    }

    /** Load metadata from model*/
    private fun getModelMetadata(litertBuffer: ByteBuffer): List<String> {
        val metadataExtractor = MetadataExtractor(litertBuffer)
        val labels = mutableListOf<String>()
        if (metadataExtractor.hasMetadata()) {
            val inputStream = metadataExtractor.getAssociatedFile("labels_without_background.txt")
            labels.addAll(readFileInputStream(inputStream))
            Log.i(
                TAG, "Successfully loaded model metadata ${metadataExtractor.associatedFileNames}"
            )
        }
        return labels
    }

    /** Retrieve Map<String, Int> from metadata file */
    private fun readFileInputStream(inputStream: InputStream): List<String> {
        val reader = BufferedReader(InputStreamReader(inputStream))

        val list = mutableListOf<String>()
        var index = 0
        var line = ""
        while (reader.readLine().also { if (it != null) line = it } != null) {
            list.add(line)
            index++
        }

        reader.close()
        return list
    }

    enum class Delegate {
        CPU, NNAPI
    }

    enum class Model(val fileName: String) {
        EfficientNet2("efficientnet_lite2.tflite"), EfficientNet("effecientnet.tflite"), ResNet("resnet_model.tflite"), ResNetKR("resnetkr.tflite");
    }

    enum class QaModel(val fileName: String) {
        QaYOLO("QaModel.tflite");
    }

    data class QaResults(
        val box: List<QaBox>, val inferenceTime: Long
    )

    data class ClassificationResult(
        val categories: List<Category>, val inferenceTime: Long
    )

    data class Category(val label: String, val score: Float)

    data class QaBox(val x_1: Float, val x_2: Float, val y_1: Float, val y_2: Float)
}