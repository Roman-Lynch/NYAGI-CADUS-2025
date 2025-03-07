package com.google.aiedge.examples.imageclassification.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.google.aiedge.examples.imageclassification.language.Language
import com.google.aiedge.examples.imageclassification.view.Pages
import com.google.aiedge.examples.imageclassification.view.DevelopmentScreen
import androidx.compose.ui.Alignment
import com.google.aiedge.examples.imageclassification.view.Pages
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size.Companion.ORIGINAL
import com.google.aiedge.examples.imageclassification.view.DevelopmentScreen
import com.google.aiedge.examples.imageclassification.view.TextHeader
import com.google.aiedge.examples.imageclassification.view.Theme
import androidx.compose.foundation.layout.Column

// include all implemented model options here
private val options:List<String> = listOf("Breast", "Pregnancy", "Early Pregnancy", "Late Pregnancy", "Shoulder")

@Composable
fun CameraButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .height(300.dp)
            .width(300.dp))
}


@Composable
fun SelectorOption(optionName:String, setCurrentPage: (Pages) -> Unit = {}) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data("file:///android_asset/ModelSelectionIcons/${optionName}.png")
            .size(ORIGINAL)
            .build(),
        contentScale = ContentScale.FillBounds,
    )
//    AsyncImage(
//        model = "file:///android_asset/ModelSelectionIcons/${optionName}.png",
//        contentDescription = "Image Representing ${optionName}",
//        modifier = Modifier
//            .size(300.dp)
//    )
    Box(Modifier
        .height(300.dp)
        .width(300.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(Theme.Purple)
        .paint(painter, contentScale = ContentScale.FillBounds)
        .border(3.dp, Theme.Black, shape = RoundedCornerShape(10.dp)
        )
    ) {
        CameraButton(onClick = {setCurrentPage(Pages.Scan) })
        Box(
            Modifier
                .fillMaxWidth()
                .border(3.dp, Theme.Black, shape = RoundedCornerShape(10.dp)),

        ){
            Text("Test text String")
        }
    }
}

@Composable
fun OptionsGrid(){
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(options){ option ->
            SelectorOption(option)
        }
    }
}

// modifier here is standard modifier that applies to every page
@Composable
fun BodyRegionsPage(currentLanguage: Language, modifier: Modifier, setCurrentPage: (Pages) -> Unit) {
    Column(modifier = modifier) {
//        OptionsGrid()
        SelectorOption("Breast", setCurrentPage)
    }
}

@Preview
@Composable
fun Preview(){
    SelectorOption("Early Pregnancy")
}