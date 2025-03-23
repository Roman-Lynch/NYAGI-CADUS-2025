package com.google.aiedge.examples.imageclassification.legacy

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.aiedge.examples.imageclassification.ImageClassificationHelper

@Composable
fun ModelSelectionDebugPanel(
    modifier: Modifier = Modifier,
    onModelSelected: (ImageClassificationHelper.Model) -> Unit,
) {
    val radioOptions = ImageClassificationHelper.Model.entries.map { it.name }.toList()
    var selectedOption by remember { mutableStateOf(radioOptions.first()) }

    Column(modifier = modifier) {
        radioOptions.forEach { option ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colors.primary),
                    selected = (option == selectedOption),
                    onClick = {
                        if (selectedOption == option) return@RadioButton
                        onModelSelected(ImageClassificationHelper.Model.valueOf(option))
                        selectedOption = option
                    }, // Recommended for accessibility with screen readers
                )
                Text(
                    modifier = Modifier.padding(start = 16.dp), text = option, fontSize = 15.sp
                )
            }
        }
    }
}