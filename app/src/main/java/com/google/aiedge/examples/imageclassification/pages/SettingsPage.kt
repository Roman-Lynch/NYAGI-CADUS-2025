package com.google.aiedge.examples.imageclassification.pages

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.google.aiedge.examples.imageclassification.language.Language
import com.google.aiedge.examples.imageclassification.language.SettingsPageText
import com.google.aiedge.examples.imageclassification.toAndroidPath
import com.google.aiedge.examples.imageclassification.view.ContentDivider
import com.google.aiedge.examples.imageclassification.view.TextHeader
import com.google.aiedge.examples.imageclassification.view.Theme

@Preview
@Composable
fun SettingsPage(
    currentLanguage: Language = Language.English,
    setLanguage: (Language) -> Unit = {}
) {

    Column(
        modifier = Modifier
            .padding(Theme.StandardPageMargin)
            .verticalScroll(rememberScrollState())
            .height(1000.dp)
    ) {
        TextHeader(SettingsPageText.title.get(currentLanguage))
        Row() {
            AsyncImage(
                model = toAndroidPath("Icons/LanguageIcon.png"),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.CenterVertically),
                contentScale = ContentScale.Fit
            )
            Text(SettingsPageText.language.get(currentLanguage),
                modifier= Modifier
                    .padding(vertical=25.dp)
                    .align(Alignment.CenterVertically),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
        ContentDivider()
        FlagsPanel(currentLanguage, setLanguage)
    }
}

@Preview
@Composable
fun LanguageIcon(
    language: Language = Language.English,
    currentLanguage: Language = Language.English,
    setCurrentLanguage: (Language) -> Unit = {}
) {

    var textHeight by remember { mutableStateOf(0) }
    val isSelected = currentLanguage == language

    fun onClick(language: Language) {
        setCurrentLanguage(language)
    }

    Column(
        modifier = Modifier
            .height(160.dp)
            .width(160.dp)
            .clickable(onClick={onClick(language)})
            .border(
                if (isSelected) 10.dp else 2.dp,
                if (isSelected) Theme.NyagiPurple else Theme.NyagiGreen,
                shape = RoundedCornerShape(10.dp)
            )
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

@Composable
fun FlagsPanel(currentLanguage: Language, setLanguage: (Language) -> Unit) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(Language.entries.chunked(2)) { rowItems ->
            Row(modifier = Modifier
                .fillMaxWidth()
            ) {
                rowItems.forEach { language ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                    ) {
                        LanguageIcon(language, currentLanguage, setLanguage)
                    }
                }
                val isAloneOnLastRow = rowItems.size == 1
                if (isAloneOnLastRow) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
