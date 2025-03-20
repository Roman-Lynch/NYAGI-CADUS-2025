package com.google.aiedge.examples.imageclassification.pages

import androidx.compose.runtime.*
import com.google.aiedge.examples.imageclassification.language.GalleryText
import com.google.aiedge.examples.imageclassification.language.Language
import com.google.aiedge.examples.imageclassification.view.TextHeader

@Composable
fun GalleryPage(currentLanguage: Language) {

    var searchText: String by remember { mutableStateOf("") }
    fun setSearchText(newText: String) { searchText = newText }

    TextHeader(GalleryText.gallery.get(currentLanguage))
    SearchBar(searchText, ::setSearchText)
}