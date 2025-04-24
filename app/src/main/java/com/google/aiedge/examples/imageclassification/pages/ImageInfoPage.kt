package com.google.aiedge.examples.imageclassification.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.aiedge.examples.imageclassification.MainViewModel
import com.google.aiedge.examples.imageclassification.view.ContentDivider
import com.google.aiedge.examples.imageclassification.view.Theme
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// Include the TextCategory and CategoryName functions from your code
@Composable
fun CategoryName(name: String, fontSize: Int) {
    Text(name, fontWeight = FontWeight.Bold, fontSize = fontSize.sp)
}

@Composable
fun TextCategory(categoryName: String, value: String) {
    val fontSize = 20
    Column {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CategoryName(categoryName, fontSize)

            // Add a minimum width spacer to ensure separation
            Spacer(modifier = Modifier.width(40.dp))

            Text(
                text = value,
                fontSize = fontSize.sp,
                modifier = Modifier.weight(1f, fill = false),
                maxLines = 1
            )
        }
    }
}

@Composable
fun ImageInfoPage(mainViewModel: MainViewModel, modifier: Modifier = Modifier) {
    val imagePath = mainViewModel.selectedImagePath.value
    val imageTime = mainViewModel.selectedImageTime.value
    val imageLabel = mainViewModel.selectedImageLabel.value

    // If no image is selected, show a default message or go back
    if (imagePath == null) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("No image selected")
            Button(onClick = { mainViewModel.popPage() }) {
                Text("Go Back")
            }
        }
        return
    }

    // Extract file name from path
    val fileName = imagePath.substringAfterLast("/")
    val scanID = fileName.substringBeforeLast(".")

    // Format the date more nicely if possible
    val formattedDate = try {
        // If imageTime is just a time, use current date
        if (imageTime?.contains(":") == true && !imageTime.contains(",")) {
            val dateFormat = SimpleDateFormat("MMMM d, h:mm:ssa", Locale.US)
            val today = Calendar.getInstance().time
            dateFormat.format(today).replace(dateFormat.format(today).substringAfter(",").substringBefore(imageTime), ", ") + imageTime
        } else {
            imageTime ?: "Unknown"
        }
    } catch (e: Exception) {
        imageTime ?: "Unknown"
    }

    // Get confidence level from label if available
    val confidence = if (imageLabel?.contains("%") == true) {
        imageLabel.substringAfterLast(" ").trim()
    } else {
        "N/A"
    }

    // Get scan type - this would typically come from the data, but we'll use a default for now
    val scanType = "Breast" // Default value, replace with actual data when available

    // State for label input
    var labelInput by remember { mutableStateOf(imageLabel ?: "") }

    Column(modifier = modifier.padding(16.dp)) {
        // Display the image using the PageImage style from your code
        PageImageFromFile(imagePath)

        ContentDivider(Theme.Grey)
        Spacer(Modifier.height(25.dp))

        // Display image information using TextCategory style
        TextCategory("Date", formattedDate)
        Spacer(Modifier.height(20.dp))

        TextCategory("Scan Type", scanType)
        Spacer(Modifier.height(20.dp))

        TextCategory("Confidence", confidence)
        Spacer(Modifier.height(20.dp))

        TextCategory("Scan ID", scanID)
        Spacer(Modifier.height(20.dp))

        // Label input field
        LabelInputField(initialValue = imageLabel ?: "", onValueChange = { labelInput = it })

        Spacer(Modifier.height(30.dp))

        // Add a back button
        Button(onClick = { mainViewModel.popPage() }) {
            Text("Back to Gallery")
        }
    }
}

@Composable
fun PageImageFromFile(filePath: String) {
    val file = File(filePath)
    val painter = rememberAsyncImagePainter(file)

    Column {
        Spacer(Modifier.height(25.dp))
        // the image itself
        Box(
            Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(10.dp))
        ) {
            Image(
                painter = painter,
                contentDescription = "Selected image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(Modifier.height(25.dp))
    }
}

@Composable
fun LabelInputField(initialValue: String, onValueChange: (String) -> Unit) {
    var input by remember { mutableStateOf(initialValue) }

    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Label",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(Modifier.width(50.dp))
        Box(
            Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Theme.Grey)
                .border(3.dp, Theme.Grey, shape = RoundedCornerShape(10.dp))
        ) {
            TextField(
                value = input,
                onValueChange = {
                    input = it
                    onValueChange(it)
                },
                label = { Text("Set label...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Theme.Grey,
                    focusedIndicatorColor = Theme.Grey,
                    unfocusedIndicatorColor = Theme.Grey
                )
            )
        }
    }
}