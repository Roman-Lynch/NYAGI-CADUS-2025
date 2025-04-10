package com.google.aiedge.examples.imageclassification.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.YuvImage
import android.util.Log
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

object ImageConverter {
    fun useByteArrayData(imageProxy: ImageProxy, handleByteData: (ByteArray) -> Unit) {
        try {
            // Convert bitmap directly from the provided imageProxy
            val bitmap = imageProxyToBitmap(imageProxy)

            // Convert bitmap to JPEG bytes
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            val jpegData = outputStream.toByteArray()

            // Call the handler
            handleByteData(jpegData)

            // Clean up
            bitmap.recycle()
        } catch (e: Exception) {
            Log.e("ImageConverter", "Error converting image: ${e.message}", e)

            try {
                // Create a blank placeholder image as fallback
                val placeholderBitmap = Bitmap.createBitmap(
                    imageProxy.width,
                    imageProxy.height,
                    Bitmap.Config.ARGB_8888
                )
                val outputStream = ByteArrayOutputStream()
                placeholderBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                handleByteData(outputStream.toByteArray())
                placeholderBitmap.recycle()
            } catch (e2: Exception) {
                Log.e("ImageConverter", "Failed to create placeholder: ${e2.message}")
                handleByteData(ByteArray(100)) // Empty fallback
            }
        }
        // Don't close the imageProxy here - it might be closed elsewhere
    }

    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap {
        val buffer = imageProxy.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun convertImageProxyToBitmap(imageProxy: ImageProxy): ByteArray {
        val bitmap = imageProxy.toBitmap()
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        return outputStream.toByteArray()
    }

    private fun convertImageProxyToYuvImage(imageProxy: ImageProxy): YuvImage {
        val planes = imageProxy.planes

        // Safety check - should already be handled by caller
        if (planes.size < 3) {
            throw IllegalArgumentException("ImageProxy has fewer than 3 planes")
        }

        val yPlane = planes[0]
        val uPlane = planes[1]
        val vPlane = planes[2]

        // Extract Y, U, V data from the ImageProxy
        val yBuffer: ByteBuffer = yPlane.buffer
        val uBuffer: ByteBuffer = uPlane.buffer
        val vBuffer: ByteBuffer = vPlane.buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)
        yBuffer.get(nv21, 0, ySize)
        uBuffer.get(nv21, ySize, uSize)
        vBuffer.get(nv21, ySize + uSize, vSize)

        return YuvImage(nv21, ImageFormat.NV21, imageProxy.width, imageProxy.height, null)
    }

    private fun compressYuvImageToJpeg(yuvImage: YuvImage): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        yuvImage.compressToJpeg(
            android.graphics.Rect(0, 0, yuvImage.width, yuvImage.height),
            90, // JPEG quality (0-100)
            byteArrayOutputStream
        )
        return byteArrayOutputStream.toByteArray()
    }

    // Extension function to convert ImageProxy to Bitmap
    private fun ImageProxy.toBitmap(): Bitmap {
        val buffer = planes[0].buffer
        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * width

        // Create bitmap
        val bitmap = Bitmap.createBitmap(
            width + rowPadding / pixelStride,
            height,
            Bitmap.Config.ARGB_8888
        )

        buffer.rewind()
        bitmap.copyPixelsFromBuffer(buffer)

        // If we need to crop due to padding
        return if (rowPadding > 0) {
            Bitmap.createBitmap(bitmap, 0, 0, width, height)
        } else {
            bitmap
        }
    }
}