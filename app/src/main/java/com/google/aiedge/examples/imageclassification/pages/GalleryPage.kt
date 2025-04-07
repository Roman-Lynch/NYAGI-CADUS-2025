package com.google.aiedge.examples.imageclassification.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.aiedge.examples.imageclassification.components.SearchBar
import com.google.aiedge.examples.imageclassification.data.GalleryImage
import com.google.aiedge.examples.imageclassification.data.GalleryImages
import com.google.aiedge.examples.imageclassification.language.GalleryText
import com.google.aiedge.examples.imageclassification.language.Language
import com.google.aiedge.examples.imageclassification.view.TextHeader
import com.google.aiedge.examples.imageclassification.view.Theme

@Preview
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
    }
}