package com.google.aiedge.examples.imageclassification.cameraComponents

import android.content.Context
import android.graphics.Bitmap
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import kotlin.math.max
import kotlin.math.min

val SHOW_MASK = false

@Composable
fun ColoredCameraBorder(
    boxColor: Color,
    bbox: FloatArray?, // Expecting [x1, y1, x2, y2]
    confidence: Float,
    maskExists: Boolean = false,
    mask: Bitmap = Bitmap.createBitmap(640, 640, Bitmap.Config.ARGB_8888),
    screenHeight: Int,
    screenWidth: Int
) {
    Box(
        modifier = Modifier.fillMaxSize().drawWithContent {
            drawContent()

            if (bbox != null && bbox.size == 4) {
                // Ensure coordinates are in correct order
                val x1 = min(bbox[0], bbox[2])
                val y1 = min(bbox[1], bbox[3])
                val x2 = max(bbox[0], bbox[2])
                val y2 = max(bbox[1], bbox[3])

                // Calculate width and height for the bounding box
                val width = x2 - x1
                val height = y2 - y1

                // Calculate bounding box area and screen area
                val bboxArea = width * height
                val screenArea = screenWidth * screenHeight

                // Compute percentage
                val bboxPercentage = (bboxArea.toFloat() / screenArea.toFloat()) * 100
                val textToDisplay = if (bboxPercentage > 25) {
                    "Confidence: ${"%.2f".format(confidence * 100)}%"
                } else {
                    "Size: ${"%.2f".format(bboxPercentage)}%"
                }

                Log.d("DrawBbox", "Corrected Bbox - Width: $width, Height: $height")
                Log.d("DrawBbox", "Corrected Offset - X: $x1, Y: $y1")
                Log.d("DrawBbox", "Bbox occupies ${"%.2f".format(bboxPercentage)}% of the screen")

                if (width > 0 && height > 0) {
                    drawRect(
                        color = boxColor,
                        topLeft = Offset(x1, y1),
                        size = Size(width, height),
                        style = Stroke(width = 5f)
                    )

                    drawIntoCanvas { canvas ->
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.WHITE
                            textSize = 40f
                            isAntiAlias = true
                        }

                        val textWidth = paint.measureText(textToDisplay)
                        val textHeight = paint.textSize

                        // Set padding around text background
                        val padding = 10f

                        val bgPaint = android.graphics.Paint().apply {
                            color = boxColor.hashCode() // Convert Compose Color to Android Color
                            style = android.graphics.Paint.Style.FILL
                        }

                        val textX = x1
                        val textY = y1 - 10
                        val bgRectLeft = textX - padding
                        val bgRectTop = textY - textHeight - padding
                        val bgRectRight = textX + textWidth + padding
                        val bgRectBottom = textY + padding

                        // Draw background rectangle first
                        canvas.nativeCanvas.drawRect(bgRectLeft, bgRectTop, bgRectRight, bgRectBottom, bgPaint)

                        // Draw text on top of rectangle
                        canvas.nativeCanvas.drawText(textToDisplay, textX, textY, paint)
                    }
                }
            }
        }
    )

    // Visualize the mask if it exists
    if (maskExists && SHOW_MASK) {
        Log.d("DrawMask", "Mask received with size [${mask.width}, ${mask.height}]")

        Image(
            painter = BitmapPainter(mask.asImageBitmap()),
            contentDescription = "Mask visualization",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}