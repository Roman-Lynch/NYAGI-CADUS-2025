package com.google.aiedge.examples.imageclassification.cameraComponents

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import kotlin.math.max
import kotlin.math.min

@Composable
fun ColoredCameraBorder(
    color: Color,
    bbox: FloatArray?, // Expecting [x1, y1, x2, y2]
    maskExists: Boolean = false,
    mask: Bitmap = Bitmap.createBitmap(640, 640, Bitmap.Config.ARGB_8888)
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

                // Calculate width and height for the rectangle
                val width = x2 - x1
                val height = y2 - y1

                Log.d("DrawBbox", "Corrected Bbox - Width: $width, Height: $height")
                Log.d("DrawBbox", "Corrected Offset - X: $x1, Y: $y1")

                if (width > 0 && height > 0) {
                    drawRect(
                        color = color,
                        topLeft = Offset(x1, y1),
                        size = Size(width, height),
                        style = Stroke(width = 5f)
                    )
                }
            }
        }
    )

    // Visualize the mask if it exists
    if (maskExists) {
        Log.d("DrawMask", "Mask received with size [${mask.width}, ${mask.height}]")

        Image(
            painter = BitmapPainter(mask.asImageBitmap()),
            contentDescription = "Mask visualization",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}