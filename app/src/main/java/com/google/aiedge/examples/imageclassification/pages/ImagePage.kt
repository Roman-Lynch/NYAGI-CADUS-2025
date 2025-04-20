package com.google.aiedge.examples.imageclassification.pages
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size.Companion.ORIGINAL
import com.google.aiedge.examples.imageclassification.view.ContentDivider
import com.google.aiedge.examples.imageclassification.view.Theme

@Composable
fun CategoryName(name:String, fontSize:Int){
    Text(name, fontWeight = FontWeight.Bold, fontSize = fontSize.sp)
}

@Composable
fun TextCategory(categoryName:String, value:String){
    val fontSize = 20
    Column(){
        Row (Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            CategoryName(categoryName, fontSize)
            Text(value, fontSize = fontSize.sp)
        }
    }
}

@Preview
@Composable
fun CategoryPreview(){
    TextCategory("Category Name Test", "Test Value")
}

@Composable
fun PageImage(filepath:String){
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            // this will need to be changed based off how these images are stored
            .data("file:///android_asset/${filepath}")
            .size(ORIGINAL)
            .build()
    )
    Column(){
        Spacer(Modifier.height(25.dp))
        // the image itself
        Box(Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .paint(painter, contentScale = ContentScale.Crop)
        )
        Spacer(Modifier.height(25.dp))
    }

}

@Preview
@Composable
fun LabelInput(){
    var input by remember{ mutableStateOf("")}
    Row(Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CategoryName("Label", 20)
        Spacer(Modifier.width(50.dp))
        Box(
            Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Theme.Grey)
                .border(3.dp, Theme.Grey, shape = RoundedCornerShape(10.dp))
        ) {
            TextField(
                value = input,
                onValueChange = { input = it },
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

// adjust these parameters as fit
// add functionality to label changer
@Composable
fun ImagePage(modifier: Modifier){
    // mockup parameters for previewing purposes
    val filepath = "mal_test.png"
    val date = "April 3, 2025, 3:00:29PM"
    val scanType = "Breast"
    val confidence = "90%"
    val scanID = "1234"

    Column(modifier = modifier){
        PageImage(filepath)
        ContentDivider(Theme.Grey)
        Spacer(Modifier.height(25.dp))
        TextCategory("Date", date)
        Spacer(Modifier.height(20.dp))
        TextCategory("Scan Type", scanType)
        Spacer(Modifier.height(20.dp))
        TextCategory("Confidence", confidence)
        Spacer(Modifier.height(20.dp))
        TextCategory("Scan ID", scanID)
        Spacer(Modifier.height(10.dp))
        LabelInput()
    }
}