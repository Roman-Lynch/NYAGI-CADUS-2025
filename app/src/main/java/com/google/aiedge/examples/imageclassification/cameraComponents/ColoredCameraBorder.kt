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

@Composable
fun ColoredCameraBorder(
    color: Color
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
            }
            .drawWithContent {
                drawContent()

                val canvasWidth = size.width
                val canvasHeight = size.height


                val width = canvasWidth * .9f
                val height = canvasHeight * .95f
                drawRect(
                    color = Color(0xFFFFFFFF),
                    topLeft = Offset(x = (canvasWidth - width) / 2, y = (canvasHeight - height) / 2),
                    size = Size(width, height),
                    blendMode = BlendMode.DstOut
                )
            }
            .background(color.copy(alpha = 0.3f))
    ) {

    }
}