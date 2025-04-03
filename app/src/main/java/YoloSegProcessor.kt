import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import com.google.aiedge.examples.imageclassification.ImageClassificationHelper.Companion.DEFAULT_MODEL
import com.google.aiedge.examples.imageclassification.ImageClassificationHelper.Model
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.exp

object YoloSegProcessor {

    data class Detection(
        val predIdx: Int,
        val confidence: Float,
        val bbox: FloatArray,
        val maskProto: FloatArray? = null,
        var processedMask: Bitmap? = null
    ) {
        // Make sure to free resources when no longer needed
        fun recycle() {
            processedMask?.recycle()
            processedMask = null
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Detection) return false
            if (predIdx != other.predIdx) return false
            if (confidence != other.confidence) return false
            if (!bbox.contentEquals(other.bbox)) return false
            if (maskProto != null) {
                if (other.maskProto == null) return false
                if (!maskProto.contentEquals(other.maskProto)) return false
            } else if (other.maskProto != null) return false
            return true
        }

        override fun hashCode(): Int {
            var result = predIdx
            result = 31 * result + confidence.hashCode()
            result = 31 * result + bbox.contentHashCode()
            result = 31 * result + (maskProto?.contentHashCode() ?: 0)
            return result
        }
    }

    fun getHighestConfidenceDetection(
        hasMasks: Boolean = true,
        model: Interpreter?,
        image: TensorImage
    ): Detection? {
        /**** HOLDER VARS ****/
        val MODEL_WIDTH = 160
        val MODEL_HEIGHT = 160
        val MODEL_SIZE = 160f
        //525 for 160x160
        // 2100 for 320x320
        // 8400 for 640x640
        val NUM_INFRENECES = 525
        val FEATURE_MAP_WIDTH = 40
        val FEATURE_MAP_HEIGHT = 40
        val NUM_CHANNELS = 32
        val DEFAULT_CONF_THRESH = 0.2f

        val bitmap = image.bitmap
        val inputSize = MODEL_WIDTH

        // Avoid creating intermediates when possible
        val inputBuffer = if (bitmap.width == inputSize && bitmap.height == inputSize) {
            convertBitmapToByteBuffer(bitmap, MODEL_WIDTH)
        } else {
            // Create scaled bitmap only when needed
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
            val buffer = convertBitmapToByteBuffer(scaledBitmap, MODEL_WIDTH)
            // Recycle immediately after use
            if (scaledBitmap != bitmap) scaledBitmap.recycle()
            buffer
        }

        // Use smaller output arrays when possible
        val outputSize = NUM_INFRENECES
        val numClasses = NUM_CHANNELS  // Reduced from assuming 37 - seems like 5 box params + 32 mask params

        // Allocate only what's needed for detection output
        val detectionOutput = Array(1) { Array(5 + numClasses) { FloatArray(outputSize) } }

        // Only allocate mask output if needed
        val maskOutput = if (hasMasks) {
            Array(1) { Array(FEATURE_MAP_WIDTH) { Array(FEATURE_MAP_WIDTH) { FloatArray(numClasses) } } }
        } else null

        val outputsMap = hashMapOf<Int, Any>()
        outputsMap[0] = detectionOutput
        if (hasMasks) {
            maskOutput?.let { outputsMap[1] = it }
        }

        try {
            model?.runForMultipleInputsOutputs(arrayOf(inputBuffer), outputsMap)
        } catch (e: Exception) {
            Log.e("YoloSegProcessor", "Error running model: ${e.message}")
            return null
        }

        val confidenceIndex = 4
        val confidenceThresh = DEFAULT_CONF_THRESH

        // Find best detection index without creating additional collections
        var bestIdx = -1
        var bestConfidence = confidenceThresh

        for (i in 0 until outputSize) {
            val confidence = detectionOutput[0][confidenceIndex][i]
            if (confidence > bestConfidence) {
                bestConfidence = confidence
                bestIdx = i
            }
        }

        // Return null if no good detection found
        if (bestIdx == -1) {
            return null
        }

        // Extract only the needed data for the best detection
        val bbox = FloatArray(4)
        for (i in 0 until 4) {
            bbox[i] = detectionOutput[0][i][bestIdx]
        }

        val maskProto = if (hasMasks) {
            FloatArray(numClasses).also { proto ->
                for (i in 0 until numClasses) {
                    proto[i] = detectionOutput[0][5 + i][bestIdx]
                }
            }
        } else null

        // Create the detection object before processing mask
        val detection = Detection(
            predIdx = bestIdx,
            confidence = bestConfidence,
            bbox = bbox,
            maskProto = maskProto
        )

        // Process mask only if needed and valid
        if (hasMasks && maskProto != null && maskOutput != null) {
            detection.processedMask = decodeMask(
                maskCoeffs = maskProto,
                maskProtos = maskOutput,
                originalWidth = bitmap.width,
                originalHeight = bitmap.height,
                maskWidth = FEATURE_MAP_WIDTH,
                maskHeight = FEATURE_MAP_HEIGHT,
                modelSize = MODEL_SIZE
            )
        }

        Log.d("QA Detection", "Detection complete!")
        Log.d("QA Detection", "Best detection confidence: ${detection.confidence}")
        detection.processedMask?.let {
            Log.d("QA Detection", "Mask dimensions: ${it.width}x${it.height}")
        }

        return detection
    }

    private fun decodeMask(
        maskCoeffs: FloatArray,
        maskProtos: Array<Array<Array<FloatArray>>>,
        originalWidth: Int,
        originalHeight: Int,
        bbox: FloatArray? = null, // Add bounding box parameter for cropping
        maskHeight: Int,
        maskWidth: Int,
        modelSize: Float
    ): Bitmap {
       // Create bitmap directly rather than going through an IntArray
        val maskBitmap = Bitmap.createBitmap(maskWidth, maskHeight, Bitmap.Config.ALPHA_8)
        val canvas = Canvas(maskBitmap)
        val paint = Paint().apply {
            color = Color.WHITE
        }

        // Process one row at a time to minimize memory usage
        for (y in 0 until maskHeight) {
            for (x in 0 until maskWidth) {
                var maskValue = 0f

                // Apply the coefficients to the prototype masks
                for (protoIdx in maskCoeffs.indices) {
                    maskValue += maskProtos[0][y][x][protoIdx] * maskCoeffs[protoIdx]
                }

                // Apply sigmoid to get value between 0 and 1
                maskValue = 1.0f / (1.0f + exp(-maskValue.toDouble())).toFloat()

                // Add a threshold to create cleaner mask boundaries
                if (maskValue > 0.5f) {
                    // Set alpha at this pixel
                    paint.alpha = (maskValue * 255).toInt()
                    canvas.drawPoint(x.toFloat(), y.toFloat(), paint)
                }
            }
        }

        // Resize to original dimensions
        val resultBitmap = Bitmap.createScaledBitmap(
            maskBitmap,
            originalWidth,
            originalHeight,
            true
        )

        // Clean up the temporary bitmap
        maskBitmap.recycle()

        // If we have a bounding box, crop the mask to it
        if (bbox != null) {
            try {

                val scaleX = originalWidth / modelSize
                val scaleY = originalHeight / modelSize

                // YOLO format is centerX, centerY, width, height - convert to x1,y1,x2,y2
                val centerX = bbox[0] * scaleX
                val centerY = bbox[1] * scaleY
                val width = bbox[2] * scaleX
                val height = bbox[3] * scaleY

                val x1 = (centerX - width / 2).coerceAtLeast(0f).toInt()
                val y1 = (centerY - height / 2).coerceAtLeast(0f).toInt()
                val x2 = (centerX + width / 2).coerceAtMost(originalWidth.toFloat()).toInt()
                val y2 = (centerY + height / 2).coerceAtMost(originalHeight.toFloat()).toInt()

                // Create a cropped bitmap with a safety check
                if (x2 > x1 && y2 > y1) {
                    val croppedWidth = x2 - x1
                    val croppedHeight = y2 - y1

                    // Create a black bitmap to combine with the mask
                    val blackBitmap = Bitmap.createBitmap(originalWidth, originalHeight, Bitmap.Config.ALPHA_8)
                    val blackCanvas = Canvas(blackBitmap)
                    val blackPaint = Paint().apply {
                        color = Color.BLACK
                        alpha = 255
                    }

                    // Draw a white rectangle for the bounding box area
                    val rectPaint = Paint().apply {
                        color = Color.WHITE
                        alpha = 255
                    }
                    blackCanvas.drawRect(x1.toFloat(), y1.toFloat(), x2.toFloat(), y2.toFloat(), rectPaint)

                    // Create a new bitmap to hold the result
                    val croppedMask = Bitmap.createBitmap(originalWidth, originalHeight, Bitmap.Config.ALPHA_8)
                    val croppedCanvas = Canvas(croppedMask)

                    // Create a paint that will combine the two bitmaps (bitwise AND)
                    val combinePaint = Paint().apply {
                        alpha = 255
                    }

                    // Combine the mask with the bounding box
                    croppedCanvas.drawBitmap(resultBitmap, 0f, 0f, combinePaint)
                    croppedCanvas.drawBitmap(blackBitmap, 0f, 0f, Paint().apply {
                        xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.DST_IN)
                    })

                    // Clean up
                    blackBitmap.recycle()
                    resultBitmap.recycle()

                    return croppedMask
                }
            } catch (e: Exception) {
                Log.e("YoloSegProcessor", "Error cropping mask: ${e.message}")
                // Return the original mask if cropping fails
            }
        }

        return resultBitmap
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap, inputSize: Int): ByteBuffer {
        // Use ByteBuffer instead of FloatBuffer to save memory (quantized approach)
        val byteBuffer = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4)
            .order(ByteOrder.nativeOrder())

        val intValues = IntArray(inputSize * inputSize)
        bitmap.getPixels(intValues, 0, inputSize, 0, 0, inputSize, inputSize)

        var pixel = 0
        for (i in 0 until inputSize) {
            for (j in 0 until inputSize) {
                val value = intValues[pixel++]

                // Extract and normalize RGB values
                byteBuffer.putFloat(((value shr 16) and 0xFF) / 255.0f)
                byteBuffer.putFloat(((value shr 8) and 0xFF) / 255.0f)
                byteBuffer.putFloat((value and 0xFF) / 255.0f)
            }
        }

        byteBuffer.rewind()
        return byteBuffer
    }
}