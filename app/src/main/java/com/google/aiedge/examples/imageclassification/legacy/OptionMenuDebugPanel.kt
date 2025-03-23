package com.google.aiedge.examples.imageclassification.legacy

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OptionMenuDebugPanel(
    label: String,
    modifier: Modifier = Modifier,
    options: List<String>,
    onOptionSelected: (option: String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var option by remember { mutableStateOf(options.first()) }
    Row(
        modifier = modifier, verticalAlignment = Alignment.CenterVertically
    ) {
        Text(modifier = Modifier.weight(0.5f), text = label, fontSize = 15.sp)
        Box {
            Row(
                modifier = Modifier.clickable {
                    expanded = true
                }, verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = option, fontSize = 15.sp)
                Spacer(modifier = Modifier.width(5.dp))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Localized description"
                )
            }

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach {
                    DropdownMenuItem(
                        content = {
                            Text(it, fontSize = 15.sp)
                        },
                        onClick = {
                            option = it
                            onOptionSelected(option)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}