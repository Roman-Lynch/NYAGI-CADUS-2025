package com.google.aiedge.examples.imageclassification.pages

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.ContentValues
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.withContext
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size.Companion.ORIGINAL
import com.google.aiedge.examples.imageclassification.MainViewModel
import com.google.aiedge.examples.imageclassification.components.SearchBar
import com.google.aiedge.examples.imageclassification.data.GalleryImage
import com.google.aiedge.examples.imageclassification.data.GalleryImages
import com.google.aiedge.examples.imageclassification.language.GalleryText
import com.google.aiedge.examples.imageclassification.language.Language
import com.google.aiedge.examples.imageclassification.navigation.Pages
import com.google.aiedge.examples.imageclassification.view.ContentDivider
import com.google.aiedge.examples.imageclassification.view.TextHeader
import com.google.aiedge.examples.imageclassification.view.Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

// Create a sealed class for category colors
sealed class CategoryBorderColor(val color: Color) {
    data object Normal : CategoryBorderColor(Theme.Grey)
    data object Benign : CategoryBorderColor(Color(0xFFFFD700)) // Yellow
    data object Malignant : CategoryBorderColor(Color(0xFFFF4444)) // Red

    companion object {
        fun forLabel(label: String): CategoryBorderColor {
            return when {
                label.contains("malignant", ignoreCase = true) -> Malignant
                label.contains("benign", ignoreCase = true) -> Benign
                else -> Normal
            }
        }
    }
}
// template for date headers. change as needed
@Composable
fun DateHeader(date: String){
    Column(Modifier
        .fillMaxWidth())
    {
        Spacer(Modifier.height(10.dp))
        Text(date,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(5.dp))
        ContentDivider(Theme.Grey)
        Spacer(Modifier.height(10.dp))
    }
}

// this is a template for individual image tiles. change data types/input as needed.
@Composable
fun ImageTile(
    imageFile: String,
    time: String,
    imageLabel: String = "",
    mainViewModel: MainViewModel,
    onDelete: () -> Unit = {},
    onDownload: () -> Unit = {}
){
    val context = LocalContext.current
    val file = File(context.getExternalFilesDir("gallery_images"), imageFile)

    Log.d("ImageTile", "Trying to load image from: ${file.absolutePath}, exists: ${file.exists()}")

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            // this will need to be changed based off how these images are stored
            //.data("file:///android_asset/${imageFile}")
            .data(file)
            .size(ORIGINAL)
            .build(),
        contentScale = ContentScale.Crop,
    )

    // Get the appropriate border color based on the image label
    val borderColor = CategoryBorderColor.forLabel(imageLabel).color

    // Increased tile size by ~10%
    var boxHeight = 165  // Increased from 150
    var boxWidth = 185   // Increased from 170
    var textAreaHeight = 40  // Slightly increased from 36
    var textMargin = 10

    Box(
        Modifier
            .height(boxHeight.dp)
            .width(boxWidth.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(2.dp, borderColor, shape = RoundedCornerShape(10.dp))
    ) {

        // Main image container
        Box(
            Modifier
                .height(boxHeight.dp)
                .width(boxWidth.dp)
                .clip(RoundedCornerShape(10.dp))
                .paint(painter, contentScale = ContentScale.Crop)
                .border(2.dp, borderColor, shape = RoundedCornerShape(10.dp))
        )

        // Info button in top-left corner
        Box(
            Modifier
                .align(Alignment.TopStart)
                .padding(6.dp)
                .size(28.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Theme.Black.copy(alpha = 0.7f))
                .clickable {
                    // Pass the file path and metadata to the view model
                    val imagePath = file.absolutePath
                    mainViewModel.setSelectedImageInfo(imagePath, time, imageLabel)
                    mainViewModel.pushPage(Pages.ImageInfoPage)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "i",
                color = Theme.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Delete button in top-right corner
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
                .size(28.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Theme.Black.copy(alpha = 0.7f))
                .clickable { onDelete() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "×",
                color = Theme.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Download button in bottom-right corner
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .offset(y = (-textAreaHeight - 6).dp)
                .padding(6.dp)
                .size(28.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Theme.Black.copy(alpha = 0.7f))
                .clickable { onDownload() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "↓",
                color = Theme.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Contains image label text/time
        Box(
            Modifier
                .offset(x = 0.dp, y = (boxHeight - textAreaHeight).dp)
                .height(textAreaHeight.dp)
                .fillMaxWidth()
                .background(Theme.White)
        ) {
            Row(
                Modifier
                    .height(textAreaHeight.dp)
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.width(textMargin.dp))
                Row(
                    Modifier.width(boxWidth.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(time, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width((textMargin * 2).dp))
                    Text(
                        imageLabel,
                        Modifier
                            .width((boxWidth / 2).dp)
                            .padding(vertical = 2.dp),
                        maxLines = 1
                    )
                }
                Spacer(Modifier.width(textMargin.dp))
            }
        }
    }
}

@Composable
fun ImageTileExample(mainViewModel: MainViewModel){
    ImageTile(
        "mal_test.png", "10:30:32am",
        mainViewModel = mainViewModel,
        onDelete = {},
        onDownload = {}
    )
}

@Preview
@Composable
fun DateHeaderPreview(){
    DateHeader("May 2, 2024")
}

@Composable
fun RecentImagesGrid(
    images: List<GalleryImage>,
    mainViewModel: MainViewModel,
    onDeleteImage: (GalleryImage) -> Unit = {},
    onDownloadImage: (GalleryImage) -> Unit = {}
) {
    // State to track scroll position
    val lazyListState = rememberLazyListState()

    // Coroutine scope that's bound to this composable's lifecycle
    val coroutineScope = rememberCoroutineScope()

    // Determine if scroll to top button should be visible
    val showScrollToTop by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0 || lazyListState.firstVisibleItemScrollOffset > 0
        }
    }

    // Set a fixed height for the grid
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(650.dp) // grid height can be changed here
    ) {
        if (images.isNotEmpty()) {
            // Use LazyColumn with a fixed height and the state
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(15.dp) // Reduced vertical spacing
            ) {
                items(images.chunked(2)) { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center // Center the items horizontally
                    ) {
                        rowItems.forEachIndexed { index, image ->
                            val imageFileName = "${image.scanID}.jpg"
                            ImageTile(
                                imageFile = imageFileName,
                                time = image.timeString,
                                imageLabel = image.label,
                                mainViewModel = mainViewModel,
                                onDelete = { onDeleteImage(image) },
                                onDownload = { onDownloadImage(image) }
                            )

                            // Add space between images, but not after the last one
                            if (index < rowItems.size - 1) {
                                Spacer(modifier = Modifier.width(10.dp))
                            }
                        }
                    }
                }
            }

            // Scroll to top button - centered at bottom
            AnimatedVisibility(
                visible = showScrollToTop,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Theme.Black.copy(alpha = 0.7f))
                        .clickable {
                            coroutineScope.launch {
                                lazyListState.animateScrollToItem(0)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "↑",
                        color = Theme.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No images found", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun GalleryPage(currentLanguage: Language = Language.ENGLISH, mainViewModel: MainViewModel, modifier: Modifier) {
    val context = LocalContext.current
    var searchText: String by remember { mutableStateOf("") }
    fun setSearchText(newText: String) { searchText = newText }

    // State to trigger UI refresh
    var refreshTrigger by remember { mutableStateOf(0) }

    // Initialize gallery images with refresh trigger
    val galleryImages = remember(refreshTrigger) { GalleryImages(context) }

    val sortedImages = remember(galleryImages, refreshTrigger) {
        galleryImages.getImages().sortedByDescending {
            "${it.dateString} ${it.timeString}"
        }
    }

    val imageDateList = remember(sortedImages) {
        sortedImages.map { it.dateString }
    }

    val filteredList = remember(imageDateList, searchText) {
        imageDateList.filter { it.contains(searchText, ignoreCase = true) }
    }

    // Function to delete an image
    fun deleteImage(image: GalleryImage) {
        galleryImages.deleteImage(image.scanID)
        refreshTrigger++
        Toast.makeText(context, "Image deleted", Toast.LENGTH_SHORT).show()
    }

    // Function to download an image to the device's Media/DCIM folder
    fun downloadImage(image: GalleryImage) {
        val sourceFile = File(context.getExternalFilesDir("gallery_images"), "${image.scanID}.jpg")

        if (sourceFile.exists()) {
            // Launch in IO thread
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Create a content resolver to add the image to the media store
                    val resolver = context.contentResolver
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, "Image_${image.scanID}")
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
                    }

                    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                    uri?.let {
                        resolver.openOutputStream(it)?.use { outputStream ->
                            sourceFile.inputStream().use { inputStream ->
                                inputStream.copyTo(outputStream)
                            }
                        }

                        // Switch to main thread to show toast
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Image saved to phone's gallery", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Failed to save image: ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.e("GalleryPage", "Error saving image", e)
                    }
                }
            }
        } else {
            Toast.makeText(context, "Image file not found", Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TextHeader(GalleryText.getGalleryText(context, currentLanguage))
        RecentImagesGrid(
            images = sortedImages,
            mainViewModel = mainViewModel,
            onDeleteImage = { image -> deleteImage(image) },
            onDownloadImage = { image -> downloadImage(image) }
        )
//        SearchBar(searchText, ::setSearchText)
//
//        Spacer(modifier = Modifier.height(10.dp))
//        LazyColumn {
//            items(filteredList) { item ->
//                Text(
//                    text = item,
//                    modifier = Modifier.padding(8.dp),
//                    color = Theme.Black
//                )
//            }
//        }

        // this displays components in actions as a template. delete these
        // and implement them appropriately
//        DateHeader("May 2, 2024 - THIS AND THE FOLLOWING IMAGE ARE A HARDCODED EXAMPLE")
//        ImageTile("mal_test.png", "10:30:32am", "No label")
    }
}