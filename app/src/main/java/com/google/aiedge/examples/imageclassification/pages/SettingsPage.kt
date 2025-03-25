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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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

@Composable
fun SettingsPage(
    currentLanguage: Language,  // Language parameter
    setLanguage: (Language) -> Unit, // Function to set language
    modifier: Modifier = Modifier  // Modifier for layout and styling
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .height(1000.dp)
    ) {
        // Update settings text with the current language
        TextHeader(SettingsPageText.getSettings(context, currentLanguage))

        // language selection
        SettingsSectionTitle(SettingsPageText.getLanguage(context, currentLanguage), "LanguageIcon")
        FlagsPanel(currentLanguage, setLanguage = setLanguage)  // Directly pass setLanguage

        Spacer(modifier = Modifier.height(30.dp))

        // about section
        SettingsSectionTitle("About", "Info")
        Row {
            Text("Version: ")
            Text("1.1.0-alpha", fontWeight = FontWeight.Bold)
        }
        // about page button
        AboutPageButton()
    }
}

@Preview
@Composable
fun AboutPageButton(){
    Row(modifier = Modifier
        .clip(RoundedCornerShape(5.dp))
        .background(Theme.Grey)
        .padding(5.dp)
        .height(20.dp)
        .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically) {
        Text("About ")
        Text("NYAGI CADUS", fontWeight = FontWeight.Bold)
    }
}
@Composable
fun SettingsSectionTitle(
    title:String,
    symbolFile:String,
){
    Row(verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = toAndroidPath("Icons/${symbolFile}.png"),
            contentDescription = null,
            modifier = Modifier
                .size(30.dp)
                .align(Alignment.CenterVertically),
            contentScale = ContentScale.Fit
        )
        Text(title,
            modifier= Modifier
                .padding(horizontal = 10.dp)
                .align(Alignment.CenterVertically),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
        )
    }
    Spacer(modifier = Modifier.height(3.dp))
    ContentDivider(color = Theme.Grey)
    Spacer(modifier = Modifier.height(3.dp))
}

@Preview
@Composable
fun LanguageIcon(
    language: Language = Language.ENGLISH,
    currentLanguage: Language = Language.ENGLISH,
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
                if (isSelected) 3.dp else 2.dp,
                if (isSelected) Theme.Purple else Theme.Teal,
                shape = RoundedCornerShape(10.dp)
            )
            .semantics { contentDescription = language.name },
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
                .background(Theme.Teal)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = language.nativeName,
            style = TextStyle(
                fontSize = 24.sp,
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(40.dp)
                .onGloballyPositioned { coordinates ->
                    textHeight = coordinates.size.height
                }
            ,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
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
