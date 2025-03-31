import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import java.nio.FloatBuffer
import java.util.Arrays
import kotlin.math.min

object YoloSegProcessor {

    data class Detection(val predIdx: Int, val confidence: Float, val bbox: FloatArray, val maskProto: FloatArray? = null)

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

            Log.d("QA Detection", "Detection output: [${detectionOutput[0][0][predIdx]}, ${detectionOutput[0][1][predIdx]}, ${detectionOutput[0][2][predIdx]}, ${detectionOutput[0][3][predIdx]}, ${detectionOutput[0][confidenceIndex][predIdx]}]")
            if (confidence > confidenceThresh) {
                // This is a valid prediction, extract all its features
                val predFeatures = FloatArray(37)
                for (featureIdx in 0 until 37) {
                    predFeatures[featureIdx] = detectionOutput[0][featureIdx][predIdx]
                }

                // Log or process this prediction
                Log.d("QA Detection", "Prediction #$predIdx with confidence $confidence: ${predFeatures.sliceArray(0 until 4).joinToString(", ")}")

                if (confidence > highestConf) {
                    bestDetection = Detection(predIdx= predIdx, confidence=confidence, bbox= predFeatures.sliceArray(0 until 4))
                    highestConf = confidence
                }
            }
        }

        Log.d("QA Detection", "Detection complete!")
        Log.d("QA Detection", "Best detection: ${bestDetection}")
        return bestDetection
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