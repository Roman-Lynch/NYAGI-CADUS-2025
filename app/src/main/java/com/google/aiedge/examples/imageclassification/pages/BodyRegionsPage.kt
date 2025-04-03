package com.google.aiedge.examples.imageclassification.pages

import android.content.res.Configuration
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.google.aiedge.examples.imageclassification.language.Language
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size.Companion.ORIGINAL
import com.google.aiedge.examples.imageclassification.view.Theme
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.google.aiedge.examples.imageclassification.MainViewModel
import com.google.aiedge.examples.imageclassification.UiState
import com.google.aiedge.examples.imageclassification.language.BodyPartsPageText
import com.google.aiedge.examples.imageclassification.language.SettingsPageText
import com.google.aiedge.examples.imageclassification.navigation.Pages
import com.google.aiedge.examples.imageclassification.view.TextHeader

// include all implemented model options here
//private val options:List<String> = listOf("Breast", "Pregnancy", "Early Pregnancy", "Late Pregnancy", "Shoulder")

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
fun SelectorOption(optionName:String, mainViewModel: MainViewModel, context: Context, currentLanguage: Language) {
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
    Box(
        Modifier
            .height(300.dp)
            .width(300.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Theme.Purple)
            .paint(painter, contentScale = ContentScale.FillBounds)
            .border(3.dp, Theme.Black, shape = RoundedCornerShape(10.dp)),
    ) {
    // in dp
    val buttonSize = 180
    val textboxHeight = 50

    // converts option name to lower case to retrieve ID
    val stringID = optionName.replaceFirstChar { it.lowercase() }

    Box(Modifier
        .height(buttonSize.dp)
        .width(buttonSize.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(Theme.Purple)
        .paint(painter, contentScale = ContentScale.FillBounds)
        .border(3.dp, Theme.Grey, shape = RoundedCornerShape(10.dp)))
    {
        CameraButton(onClick = { mainViewModel.pushPage(Pages.Scan) })
        Box(Modifier
            .offset(x=0.dp, y=(buttonSize-textboxHeight).dp) // sits this at the bottom of the box
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(textboxHeight.dp)
                    .background(Theme.White)
                    .border(3.dp, Theme.Grey, shape = RoundedCornerShape(10.dp))

            ){
                Text(BodyPartsPageText.getBodyPartText(context, currentLanguage, stringID),
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 20.sp)
            }
        }
    }
}

//@Composable
//fun OptionsGrid(){
//    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
//        items(options){ option ->
////            SelectorOption(option)
//        }
//    }
//}

// modifier here is standard modifier that applies to every page
@Composable
fun BodyRegionsPage(currentLanguage: Language, modifier: Modifier, mainViewModel: MainViewModel) {
    Column(modifier = modifier) {
//        OptionsGrid()
        val context = LocalContext.current
        TextHeader(BodyPartsPageText.getBodyPartSelectorText(context, currentLanguage))
        SelectorOption("Breast", mainViewModel, context, currentLanguage)
    }
}