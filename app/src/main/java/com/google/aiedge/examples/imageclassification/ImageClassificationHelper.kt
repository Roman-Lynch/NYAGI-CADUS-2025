package com.google.aiedge.examples.imageclassification

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
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
import org.tensorflow.lite.support.common.ops.QuantizeOp
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
import java.util.Arrays

class ImageClassificationHelper(
    private val context: Context,
    private var options: Options = Options(),
) {
    // Added screen dimensions properties
    private val screenWidth: Int
    private val screenHeight: Int

    init {
        // Initialize screen dimensions in the constructor
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        screenWidth = displayMetrics.widthPixels
        screenHeight = displayMetrics.heightPixels

        Log.i(TAG, "Screen dimensions: $screenWidth x $screenHeight")
    }

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
        var threadCount: Int = DEFAULT_THREAD_COUNT,
        /** Whether to scale mask to screen size (true) or original image size (false) */
        var scaleToScreen: Boolean = true
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

                // Store the original bitmap dimensions before any processing
                val originalBitmapWidth = bitmap.width
                val originalBitmapHeight = bitmap.height

                Log.d(TAG, "Original bitmap dimensions: $originalBitmapWidth x $originalBitmapHeight")

                // TensorFlow Lite Support library's processing pipeline
                val (_, h, w, _) = qa_interpreter?.getInputTensor(0)?.shape() ?: return@withContext
                val imageProcessor = ImageProcessor.Builder()
                    .add(ResizeOp(h, w, ResizeOp.ResizeMethod.BILINEAR))
                    .add(QuantizeOp(0f, 1f))
                    .build()
                val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))

                // Pass original dimensions to runQaWithTFLite
                val output = runQaWithTFLite(tensorImage, originalBitmapWidth, originalBitmapHeight)
                val outputBbox = output.Bbox
                val outputMask = output.Mask

                val box = outputBbox.map {
                    QaBox(x_1 = outputBbox[0], x_2 = outputBbox[1], y_1 = outputBbox[2], y_2 = outputBbox[3], mask = outputMask, hasMask = true)
                }.take(options.resultCount)

                val inferenceTime = SystemClock.uptimeMillis() - startTime

                fun logQaResults() {
                    if (box.isNotEmpty()) {
                        Log.i(TAG, "QA Classification Complete complete. Box: ${box}")
                    } else {
                        Log.w(TAG, "QA Classification not complete")
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
    private fun runQaWithTFLite(
        tensorImage: TensorImage,
        originalBitmapWidth: Int,
        originalBitmapHeight: Int
    ): BboxMaskPair {
        val bestDetection = YoloSegProcessor.getHighestConfidenceDetection(model = qa_interpreter, image = tensorImage)

        Log.i(TAG, "YOLO detection output: ${bestDetection}")

        if (bestDetection != null) {
            val bbox = bestDetection.bbox // YOLO output is normalized or relative to model input size
            val mask = bestDetection.processedMask

            val tensorHeight = tensorImage.height
            val tensorWidth = tensorImage.width
            val modelWidth = 640f
            val modelHeight = 640f

            Log.d(TAG, "Tensor dimensions: $tensorWidth x $tensorHeight")

            // YOLO output is assumed to be (cx, cy, w, h)
            val cx = bbox[0] * screenWidth // Convert normalized to absolute
            val cy = bbox[1] * screenHeight
            val w = bbox[2] * screenWidth
            val h = bbox[3] * screenHeight

            // Convert to (x_min, y_min, x_max, y_max)
            val xMin = cx - (w / 2)
            val xMax = cx + (w / 2)
            val yMin = cy - (h / 2)
            val yMax = cy + (h / 2)

            // Calculate scaling factors based on whether to scale to screen or original image
            val targetWidth: Int
            val targetHeight: Int

            if (options.scaleToScreen) {
                // Scale to screen dimensions
                targetWidth = screenWidth
                targetHeight = screenHeight

                Log.d(TAG, "Scaling mask to screen dimensions: ${targetWidth}x${targetHeight}")
            } else {
                // Scale to original bitmap dimensions (not tensor dimensions)
                targetWidth = originalBitmapWidth
                targetHeight = originalBitmapHeight

                Log.d(TAG, "Scaling mask to original bitmap dimensions: ${targetWidth}x${targetHeight}")
            }

            // Scale bounding box
            val scaledBBox = floatArrayOf(
                xMin,
                xMax,
                yMin,
                yMax
            )

            // Scale the mask if it exists
            if (mask != null) {
                // Create a properly scaled mask
                val scaledMask = Bitmap.createScaledBitmap(
                    mask,
                    targetWidth,
                    targetHeight,
                    true
                )

                Log.d(TAG, "Scaled Mask Dimensions: ${scaledMask.width}x${scaledMask.height}")
                Log.d(TAG, "Scaled Bounding Box: ${scaledBBox.sliceArray(0 until 4).joinToString(", ")}")

                return BboxMaskPair(scaledBBox, scaledMask)
            } else {
                Log.d(TAG, "Bounding box was found, but no mask was found...")
                return BboxMaskPair(FloatArray(0), Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888))
            }
        } else {
            val targetWidth = if (options.scaleToScreen) screenWidth else originalBitmapWidth
            val targetHeight = if (options.scaleToScreen) screenHeight else originalBitmapHeight

            return BboxMaskPair(FloatArray(0), Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)) // Return empty if no detection
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

    data class QaBox(val x_1: Float, val x_2: Float, val y_1: Float, val y_2: Float, val mask: Bitmap, val hasMask: Boolean = false)

    data class BboxMaskPair(val Bbox: FloatArray, val Mask: Bitmap)
}