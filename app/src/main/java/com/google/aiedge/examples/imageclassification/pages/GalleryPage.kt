package com.google.aiedge.examples.imageclassification.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
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
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size.Companion.ORIGINAL
import com.google.aiedge.examples.imageclassification.components.SearchBar
import com.google.aiedge.examples.imageclassification.data.GalleryImage
import com.google.aiedge.examples.imageclassification.data.GalleryImages
import com.google.aiedge.examples.imageclassification.language.GalleryText
import com.google.aiedge.examples.imageclassification.language.Language
import com.google.aiedge.examples.imageclassification.view.ContentDivider
import com.google.aiedge.examples.imageclassification.view.TextHeader
import com.google.aiedge.examples.imageclassification.view.Theme

// template for date headers. change as needed
@Composable
fun DateHeader(date: String){
    Column(Modifier
        .fillMaxWidth())
    {
        Spacer(Modifier.height(10.dp))
        Text(date,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(5.dp))
        ContentDivider(Theme.Grey)
        Spacer(Modifier.height(10.dp))
    }
}

// this is a template for individual image tiles. change data types/input as needed.
@Composable
fun ImageTile(imageFile: String, time: String, imageLabel: String){
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            // this will need to be changed based off how these images are stored
            .data("file:///android_asset/${imageFile}")
            .size(ORIGINAL)
            .build(),
        contentScale = ContentScale.Fit,
    )
    var boxHeight = 150
    var boxWidth = 170
    var textAreaHeight = 30
    var textMargin = 10

    Box(Modifier
        .height(boxHeight.dp)
        .width(boxWidth.dp)
        .clip(RoundedCornerShape(10.dp))
        .paint(painter, contentScale = ContentScale.FillBounds)
        .border(2.dp, Theme.Grey, shape = RoundedCornerShape(10.dp)))
    {
        // contains image label text/time
        Box(Modifier
            .offset(x=0.dp, y=(boxHeight-textAreaHeight).dp)
            .height(textAreaHeight.dp)
            .background(Theme.White)// sits this at the bottom of the box
        ){
            // this row: adds side margins
            Row(Modifier
                .height(textAreaHeight.dp)
                .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Spacer(Modifier.width(textMargin.dp))
                // this row: actually contains text
                Row(
                    Modifier.width(boxWidth.dp)
                ){
                    Text(time, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width((textMargin*2).dp))
                    Text(imageLabel,
                        Modifier
                            .width((boxWidth/3).dp)
                            .height((textAreaHeight/2).dp))
                }
                Spacer(Modifier.width(textMargin.dp))
            }
        }
    }
}

@Preview
@Composable
fun ImageTileExample(){
    ImageTile("mal_test.png", "10:30:32am", "No label")
}
@Preview
@Composable
fun DateHeaderPreview(){
    DateHeader("May 2, 2024")
}

@Composable
fun GalleryPage(currentLanguage: Language = Language.ENGLISH, modifier: Modifier) {

    val context = LocalContext.current
    var searchText: String by remember { mutableStateOf("") }
    fun setSearchText(newText: String) { searchText = newText }

    val galleryImages = GalleryImages(LocalContext.current)

    val imageDateList = galleryImages.getImages().map({
        galleryImage: GalleryImage -> galleryImage.dateString
    })

    val filteredList = imageDateList.filter { it.contains(searchText, ignoreCase = true) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TextHeader(GalleryText.getGalleryText(context, currentLanguage))
        SearchBar(searchText, ::setSearchText)

        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn {
            items(filteredList) { item ->
                Text(
                    text = item,
                    modifier = Modifier.padding(8.dp),
                    color = Theme.Black
                )
            }
        }

        // this displays components in actions as a template. delete these
        // and implement them appropriately
        DateHeader("May 2, 2024 - THIS AND THE FOLLOWING IMAGE ARE A HARDCODED EXAMPLE")
        ImageTile("mal_test.png", "10:30:32am", "No label")
    }
}