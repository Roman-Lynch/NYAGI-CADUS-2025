package com.google.aiedge.examples.imageclassification.cameraComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size.Companion.ORIGINAL
import com.google.aiedge.examples.imageclassification.language.CameraText
import com.google.aiedge.examples.imageclassification.language.Language

@Composable
fun RotatePhonePopup(
    optionName: String,
    currentLanguage: Language,
    modifier: Modifier
) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data("file:///android_asset/Icons/${optionName}.png")
            .size(ORIGINAL)
            .build(),
        contentScale = ContentScale.FillBounds
    )
    Box(
        modifier = modifier
            .height(300.dp)
            .width(300.dp)
            .padding(25.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(Color.Black.copy(alpha = 0.80f), shape = RoundedCornerShape(25.dp))
            .paint(painter, alignment = Alignment.TopCenter, colorFilter = ColorFilter.tint(Color.White)),
    ) {
        Text(
            text = CameraText.getRotateScreenText(LocalContext.current, currentLanguage),
            modifier = Modifier.align(Alignment.BottomCenter).padding(5.dp),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}