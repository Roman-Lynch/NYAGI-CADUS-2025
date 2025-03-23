package com.google.aiedge.examples.imageclassification.legacy

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*

@Composable
fun AdjustItemDebugPanel(
    name: String,
    value: Number,
    modifier: Modifier = Modifier,
    onMinusClicked: () -> Unit,
    onPlusClicked: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(0.5f),
            text = name,
            fontSize = 15.sp,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                onClick = {
                    onMinusClicked()
                }) {
                Text(text = "-", fontSize = 15.sp, color = Color.White)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                modifier = Modifier.width(30.dp),
                textAlign = TextAlign.Center,
                text = if (value is Float) String.format(
                    Locale.US, "%.1f", value
                ) else value.toString(),
                fontSize = 15.sp,
            )
            Spacer(modifier = Modifier.width(10.dp))
            Button(
                onClick = {
                    onPlusClicked()
                }) {
                Text(text = "+", fontSize = 15.sp, color = Color.White)
            }
        }
    }
}