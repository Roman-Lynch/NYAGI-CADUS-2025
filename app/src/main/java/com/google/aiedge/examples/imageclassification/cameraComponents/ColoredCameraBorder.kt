package com.google.aiedge.examples.imageclassification.cameraComponents

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

@Composable
fun ColoredCameraBorder(
    color: Color,
    canvasWidth: Float,
    canvasHeight: Float,
    bbox: FloatArray? // Expecting [x1, y1, x2, y2]
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
            }
            .drawWithContent {
                drawContent()

                // Draw the bounding box only if bbox is provided
                if (bbox != null && bbox.size == 4) {
                    // Extract coordinates
                    val x1 = bbox[0]
                    val y1 = bbox[1]
                    val x2 = bbox[2]
                    val y2 = bbox[3]

                    // Calculate width and height for the rectangle
                    val width = (x2 - x1).absoluteValue
                    val height = (y2 - y1).absoluteValue

                    val offset_x = ((x1).absoluteValue + width)
                    val offset_y = ((y1).absoluteValue + height)

                    Log.d("DrawBbox", "Bbox - Width: $width, Height: $height")
                    Log.d("DrawBbox", "Offset - X: $offset_x, Y: $offset_y")

                    // Only draw if we have a valid rectangle (positive width and height)
                    if (width > 0 && height > 0) {
                        drawRect(
                            color = color,
                            topLeft = Offset((x1).absoluteValue+width, (y1).absoluteValue-height), // Corrected to use (x1, y1) as the top-left corner
                            size = Size(width, height),
                            style = Stroke(width = 5f) // Outline only
                        )
                    }
                }
            }
    )
}
