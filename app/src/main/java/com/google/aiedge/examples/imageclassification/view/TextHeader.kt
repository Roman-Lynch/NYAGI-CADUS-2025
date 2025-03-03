package com.google.aiedge.examples.imageclassification.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.aiedge.examples.imageclassification.toAndroidPath

@Composable
fun TextHeader(name:String){
    Column(){
        Row(modifier = Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(name,
                modifier= Modifier.padding(vertical=25.dp),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            AsyncImage(
                model = toAndroidPath("Icons/Help.png"),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
            )

        }
        ContentDivider()
        Spacer(modifier= Modifier.height(25.dp))
    }
}