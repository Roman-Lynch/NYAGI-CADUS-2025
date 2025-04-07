package com.google.aiedge.examples.imageclassification.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.aiedge.examples.imageclassification.toAndroidPath
import com.google.aiedge.examples.imageclassification.view.Theme

@Composable
fun SearchBar(searchText: String, setSearchText: (String) -> Unit) {

    Box(Modifier
        .clip(RoundedCornerShape(10.dp))
        .background(Theme.Grey)
        .border(3.dp, Theme.Grey, shape = RoundedCornerShape(10.dp))
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
//        AsyncImage(
//                model = toAndroidPath("Icons/Help.png"),
//                contentDescription = null,
//                modifier = Modifier
//                    .size(50.dp)
//            )
            Spacer(modifier= Modifier.width(10.dp))
            AsyncImage(
                model = toAndroidPath("Icons/SearchIcon.png"),
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp)
            )
            var input by remember{ mutableStateOf("")}
            TextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("Search...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Theme.Grey,
                    focusedIndicatorColor = Theme.Grey,
                    unfocusedIndicatorColor = Theme.Grey
                )
            )
        }
    }
}

@Preview
@Composable
fun SearchPreview(){
    var searchText: String by remember { mutableStateOf("") }
    fun setSearchText(newText: String) { searchText = newText }
    SearchBar("test", ::setSearchText )
}