package com.google.aiedge.examples.imageclassification.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.aiedge.examples.imageclassification.toAndroidPath
import com.google.aiedge.examples.imageclassification.view.ContentDivider
import com.google.aiedge.examples.imageclassification.view.TextHeader
import com.google.aiedge.examples.imageclassification.view.Theme

enum class Language(val nativeName: String, val flagPath: String) {
    English("English", "Flags/UKFlag.png"),
}

@Preview
@Composable
fun SettingsPage() {

    Column(
        modifier = Modifier
            .padding(Theme.StandardPageMargin)
    ) {
        TextHeader("Settings")
        Row() {
            AsyncImage(
                model = toAndroidPath("Icons/LanguageIcon.png"),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.CenterVertically),
                contentScale = ContentScale.Fit
            )
            Text("Language",
                modifier= Modifier
                    .padding(vertical=25.dp)
                    .align(Alignment.CenterVertically),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
        ContentDivider()
    }
}

@Preview
@Composable
fun languageIcon(
    language: Language = Language.English,
    currentLanguage: Language = Language.English,
    setCurrentLanguage: (Language) -> Unit = {}
) {

    var textHeight by remember { mutableStateOf(0) }

    fun onClick(language: Language) {
        setCurrentLanguage(language)
    }

    Column(
        modifier = Modifier
            .height(160.dp)
            .width(160.dp)
            .clickable(onClick={onClick(language)})
            .border(2.dp, Theme.NyagiGreen, shape = RoundedCornerShape(10.dp))
    ) {
        AsyncImage(
            model = toAndroidPath(language.flagPath),
            contentDescription = null,
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth()
                .border(3.dp, Color.White, shape = RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(.8f)
                .height(2.dp)
                .background(Theme.NyagiGreen)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = language.nativeName,
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                fontStyle = FontStyle.Italic,
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(40.dp)
                .onGloballyPositioned { coordinates ->
                    textHeight = coordinates.size.height
                },
        )
    }
}
