package com.google.aiedge.examples.imageclassification.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SearchBar(searchText: String, setSearchText: (String) -> Unit) {

    OutlinedTextField(
        value = searchText,
        onValueChange = { setSearchText(it) },
        label = { Text("Search...") },
        modifier = Modifier.fillMaxWidth()
    )
}