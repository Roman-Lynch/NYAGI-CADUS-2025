package com.google.aiedge.examples.imageclassification.cameraComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.absoluteValue

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
                    // IN THIS CASE, X is Y
                if (bbox != null && bbox.size == 4) {
                    val x1 = bbox[1]
                    val y1 = bbox[0]
                    val x2 = bbox[3]
                    val y2 = bbox[2]
                    val topLeftX = x1 - (x2 - x1) / 2
                    val topLeftY = y1 - (y2 - y1) / 2

                    drawRect(
                        color = color, // Color should be passed from the caller
                        topLeft = Offset(topLeftX, topLeftY),
                        size = Size(x2 - x1, y2 - y1),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 5f) // Outline only
                    )
                }
            }
    )
}