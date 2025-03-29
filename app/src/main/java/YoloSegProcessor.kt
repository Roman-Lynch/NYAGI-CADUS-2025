object YoloSegProcessor {

    data class Detection(val classId: Int, val confidence: Float, val bbox: FloatArray)

    fun getHighestConfidenceDetection(output: FloatArray, numClasses: Int): Detection? {
        if (output.isEmpty()) return null

        // Infer the output shape dynamically (assuming 1, 8400, 37 format)
        val numDetections = output.size / (numClasses + 5) // 8400

        var maxConfidence = -1f
        var bestDetection: Detection? = null

        for (i in 0 until numDetections) {
            val offset = i * (numClasses + 5)
            val confidence = output[offset + 4] // Confidence score

            if (confidence > maxConfidence) {
                maxConfidence = confidence

                // Find the class with the highest probability
                var bestClassId = 0
                var bestClassScore = 0f
                for (j in 0 until numClasses) {
                    val classScore = output[offset + 5 + j]
                    if (classScore > bestClassScore) {
                        bestClassScore = classScore
                        bestClassId = j
                    }
                }

                // Bounding box (x, y, width, height)
                val bbox = floatArrayOf(
                    output[offset],     // x
                    output[offset + 1], // y
                    output[offset + 2], // width
                    output[offset + 3]  // height
                )

                bestDetection = Detection(bestClassId, confidence, bbox)
            }
        }

        return bestDetection
    }
}