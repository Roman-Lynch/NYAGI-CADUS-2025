package com.google.aiedge.examples.imageclassification.data

import android.graphics.ImageFormat
import android.graphics.YuvImage
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

object ImageConverter {

    fun useByteArrayData(imageProxy: ImageProxy, handleByteData: (ByteArray) -> Unit) {

        val yuvImage: YuvImage = convertImageProxyToYuvImage(imageProxy)
        val jpegData: ByteArray = compressYuvImageToJpeg(yuvImage)

        handleByteData(jpegData)

        imageProxy.close()
    }

    private fun convertImageProxyToYuvImage(imageProxy: ImageProxy): YuvImage {
        val planes = imageProxy.planes
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
            100, // JPEG quality (0-100)
            byteArrayOutputStream
        )
        return byteArrayOutputStream.toByteArray()
    }

}