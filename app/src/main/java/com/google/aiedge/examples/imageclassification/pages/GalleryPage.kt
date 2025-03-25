package com.google.aiedge.examples.imageclassification.pages

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.aiedge.examples.imageclassification.components.SearchBar
import com.google.aiedge.examples.imageclassification.data.GalleryImages
import com.google.aiedge.examples.imageclassification.language.GalleryText
import com.google.aiedge.examples.imageclassification.language.Language
import com.google.aiedge.examples.imageclassification.view.TextHeader

@Composable
fun GalleryPage(currentLanguage: Language) {

    val context = LocalContext.current
    var searchText: String by remember { mutableStateOf("") }
    fun setSearchText(newText: String) { searchText = newText }

    var galleryImages: GalleryImages = GalleryImages(LocalContext.current)

    TextHeader(GalleryText.getGalleryText(context, currentLanguage))
    SearchBar(searchText, ::setSearchText)
}