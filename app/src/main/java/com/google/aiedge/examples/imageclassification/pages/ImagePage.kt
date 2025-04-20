package com.google.aiedge.examples.imageclassification.pages
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun TextCategory(categoryName:String, value:String){
    Column(){
        Row (Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(categoryName, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(value, fontSize = 20.sp)
        }
        Spacer(Modifier.height(5.dp))
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

// adjust these parameters as fit
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
        TextCategory("Scan Type", scanType)
        TextCategory("Confidence", confidence)
        TextCategory("Scan ID", scanID)
    }
}