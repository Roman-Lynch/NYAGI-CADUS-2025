package com.google.aiedge.examples.imageclassification.legacy

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.aiedge.examples.imageclassification.ImageClassificationHelper
import com.google.aiedge.examples.imageclassification.UiState
import com.google.aiedge.examples.imageclassification.R
import java.util.Locale

@Composable
fun DebugPanel(
    uiState: UiState,
    modifier: Modifier = Modifier,
    onModelSelected: (ImageClassificationHelper.Model) -> Unit,
    onDelegateSelected: (ImageClassificationHelper.Delegate) -> Unit,
    onThresholdSet: (value: Float) -> Unit
) {
    val categories = uiState.categories
    val inferenceTime = uiState.inferenceTime
    val threshold = uiState.setting.threshold
    Column(
        modifier = modifier.padding(horizontal = 20.dp, vertical = 5.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        LazyColumn {
            items(key = {
                categories[it].label
            }, count = categories.size) {
                val category = categories[it]
                Row {
                    Text(
                        modifier = Modifier.weight(0.5f),
                        text = if (category.score <= 0f) "--" else category.label,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = if (category.score <= 0f) "--" else String.format(
                            Locale.US, "%.2f", category.score
                        ),
                        fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
        Image(
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.CenterHorizontally),
            painter = painterResource(id = R.drawable.ic_chevron_up),
            colorFilter = ColorFilter.tint(MaterialTheme.colors.secondary),
            contentDescription = ""
        )
        Row {
            Text(
                modifier = Modifier.weight(0.5f),
                text = stringResource(id = R.string.inference_title)
            )
            Text(text = stringResource(id = R.string.inference_value, inferenceTime))
        }
        Spacer(modifier = Modifier.height(20.dp))
        ModelSelectionDebugPanel(onModelSelected = {
            onModelSelected(it)
        })
        Spacer(modifier = Modifier.height(20.dp))
        OptionMenuDebugPanel(label = stringResource(id = R.string.delegate),
            options = ImageClassificationHelper.Delegate.entries.map { it.name }) {
            onDelegateSelected(ImageClassificationHelper.Delegate.valueOf(it))
        }
        Spacer(modifier = Modifier.height(10.dp))
        AdjustItemDebugPanel(
            name = stringResource(id = R.string.threshold),
            value = uiState.setting.threshold,
            onMinusClicked = {
                if (threshold > 0.3f) {
                    val newThreshold = (threshold - 0.1f).coerceAtLeast(0.0f)
                    onThresholdSet(newThreshold)
                }
            },
            onPlusClicked = {
                if (threshold < 0.8f) {
                    val newThreshold = threshold + 0.1f.coerceAtMost(0.0f)
                    onThresholdSet(newThreshold)
                }
            },
        )
    }
}
