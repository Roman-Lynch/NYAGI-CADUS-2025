import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import java.nio.FloatBuffer
import java.util.Arrays
import kotlin.math.min
import kotlin.math.exp

object YoloSegProcessor {

    data class Detection(
        val predIdx: Int,
        val confidence: Float,
        val bbox: FloatArray,
        val maskProto: FloatArray? = null,
        val processedMask: Bitmap? = null
    )

    fun getHighestConfidenceDetection(
        hasMasks: Boolean = true,
        model: Interpreter?,
        image: TensorImage
    ): Detection? {
        val bitmap = image.bitmap
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 640, 640, true)
        val inputBuffer = convertBitmapToFloatBuffer(resizedBitmap)

        val detectionOutput = Array(1) { Array(37) { FloatArray(8400) } }
        val maskOutput = Array(1) { Array(160) { Array(160) { FloatArray(32) } } }

        val outputsMap = HashMap<Int, Any>()
        outputsMap[0] = detectionOutput // Detection output
        outputsMap[1] = maskOutput // Mask output

        if (model != null) {
            model.runForMultipleInputsOutputs(arrayOf(inputBuffer), outputsMap)
        }

        // GET input details
        val inputTensor = model?.getInputTensor(0)
        Log.i("QA Detection", "QA Model input details: type=${inputTensor?.dataType()}, shape=${inputTensor?.shape()?.joinToString()}")

        // GET output details
        val outputTensor = model?.getOutputTensor(0)
        val shape = outputTensor?.shape()
        Log.d("ModelOutput", "Shape [0]: ${Arrays.toString(shape)}")

        var highestConf = 0f
        var bestDetection: Detection? = null

        // Assuming confidence is at index 4 in the feature array
        val confidenceThresh = 0.5f
        val confidenceIndex = 4

        val confidenceStats = mutableListOf<Float>()
        for (predIdx in 0 until min(8400, 100)) {  // Sample first 100 predictions
            confidenceStats.add(detectionOutput[0][confidenceIndex][predIdx])
        }
        Log.d("QA Detection", "Confidence stats (first 100): min=${confidenceStats.minOrNull()}, " +
                "max=${confidenceStats.maxOrNull()}, avg=${confidenceStats.average()}")

        // Check all 8400 predictions
        for (predIdx in 0 until 8400) {
            val confidence = detectionOutput[0][confidenceIndex][predIdx]

            if (confidence > confidenceThresh) {
                // This is a valid prediction, extract all its features
                val predFeatures = FloatArray(37)
                for (featureIdx in 0 until 37) {
                    predFeatures[featureIdx] = detectionOutput[0][featureIdx][predIdx]
                }

                if (confidence > highestConf) {
                    // Extract mask coefficients (assuming they start at index 5)
                    val maskProto = if (hasMasks) {
                        predFeatures.sliceArray(5 until 37)
                    } else {
                        null
                    }

                    // Process the mask if we have mask prototypes
                    val processedMask = if (hasMasks && maskProto != null) {
                        decodeMask(maskProto, maskOutput, bitmap.width, bitmap.height)
                    } else {
                        null
                    }

                    bestDetection = Detection(
                        predIdx = predIdx,
                        confidence = confidence,
                        bbox = predFeatures.sliceArray(0 until 4),
                        maskProto = maskProto,
                        processedMask = processedMask
                    )
                    highestConf = confidence
                }
            }
        }

        Log.d("QA Detection", "Detection complete!")
        Log.d("QA Detection", "Best detection: ${bestDetection}")
        if (bestDetection?.processedMask != null) {
            Log.d("QA Detection", "Mask dimensions: ${bestDetection.processedMask!!.width}x${bestDetection.processedMask!!.height}")
        }
        return bestDetection
    }

    private fun decodeMask(
        maskCoeffs: FloatArray,
        maskProtos: Array<Array<Array<FloatArray>>>,
        originalWidth: Int,
        originalHeight: Int
    ): Bitmap {
        // Create a mask of size 160x160 (mask proto dimensions)
        val maskHeight = 160
        val maskWidth = 160
        val maskPixels = IntArray(maskWidth * maskHeight)

        // For each pixel in the mask, compute the value
        for (y in 0 until maskHeight) {
            for (x in 0 until maskWidth) {
                var maskValue = 0f

                // Apply the coefficients to the prototype masks
                for (protoIdx in 0 until 32) {
                    maskValue += maskProtos[0][y][x][protoIdx] * maskCoeffs[protoIdx]
                }

                // Apply sigmoid to get value between 0 and 1
                maskValue = 1.0f / (1.0f + exp(-maskValue.toDouble())).toFloat()

                // Convert to 0-255 range for alpha
                val alphaValue = (maskValue * 255).toInt()
                maskPixels[y * maskWidth + x] = (alphaValue shl 24) or 0xFFFFFF // White with alpha
            }
        }

        // Create bitmap from the mask
        val maskBitmap = Bitmap.createBitmap(maskWidth, maskHeight, Bitmap.Config.ARGB_8888)
        maskBitmap.setPixels(maskPixels, 0, maskWidth, 0, 0, maskWidth, maskHeight)

        // Resize to original image dimensions
        return Bitmap.createScaledBitmap(maskBitmap, originalWidth, originalHeight, true)
    }

    private fun convertBitmapToFloatBuffer(bitmap: Bitmap): FloatBuffer {
        val inputSize = 640
        val floatBuffer = FloatBuffer.allocate(1 * inputSize * inputSize * 3)
        floatBuffer.rewind()

        val intValues = IntArray(inputSize * inputSize)
        bitmap.getPixels(intValues, 0, inputSize, 0, 0, inputSize, inputSize)

        for (pixel in intValues) {
            val r = ((pixel shr 16) and 0xFF) / 255.0f // Normalize to [0,1]
            val g = ((pixel shr 8) and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f

            floatBuffer.put(r)
            floatBuffer.put(g)
            floatBuffer.put(b)
        }

        floatBuffer.rewind()
        return floatBuffer
    }
}