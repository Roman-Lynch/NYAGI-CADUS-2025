package com.google.aiedge.examples.imageclassification.pages

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.aiedge.examples.imageclassification.Components.SearchBar
import com.google.aiedge.examples.imageclassification.data.GalleryImages
import com.google.aiedge.examples.imageclassification.language.GalleryText
import com.google.aiedge.examples.imageclassification.language.Language
import com.google.aiedge.examples.imageclassification.view.TextHeader

@Composable
fun GalleryPage(currentLanguage: Language) {

    var searchText: String by remember { mutableStateOf("") }
    fun setSearchText(newText: String) { searchText = newText }

    var galleryImages: GalleryImages = GalleryImages(LocalContext.current)

    TextHeader(GalleryText.gallery.get(currentLanguage))
    SearchBar(searchText, ::setSearchText)
}