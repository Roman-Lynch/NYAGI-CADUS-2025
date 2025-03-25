package com.google.aiedge.examples.imageclassification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.aiedge.examples.imageclassification.language.CameraText
import com.google.aiedge.examples.imageclassification.language.GenericText
import com.google.aiedge.examples.imageclassification.language.Language

@Composable
fun NoCameraPermissionsAlert(onClick: () -> Unit, currentLanguage: Language) {

    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = {},
        title = { Text(CameraText.getRotateScreenText(context, currentLanguage)) },
        text = { Text(CameraText.getNoCameraPermissionsText(context, currentLanguage)) },
        buttons = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally, // Optional: Centers the buttons horizontally
                verticalArrangement = Arrangement.spacedBy(16.dp), // Optional: Adds space between the buttons
                modifier = Modifier.padding(16.dp) // Optional: Adds padding around the column
            ) {
                TextButton(onClick = onClick) {
                    Text(GenericText.getOkText(context, currentLanguage))
                }
            }
        }
    )
}