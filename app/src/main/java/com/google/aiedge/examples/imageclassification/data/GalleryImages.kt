package com.google.aiedge.examples.imageclassification.data;

import FileManager
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageProxy
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.UUID

class GalleryImagesDoesNotExistException : Exception("galleryImages does not exist when fetching from NYAGI gallery_images directory")
class CouldNotAddImageException: Exception("galleryImages could not add image to gallery_image file")

class GalleryImages(val context: Context) {
    private val imagesDirectoriesPath = "gallery_images"
    private val galleryFileName = "gallery.json"
    private val fileManager: FileManager = FileManager(context)
    private val imagesList = ArrayList<GalleryImage>()

    private val imagesDirectory: File = fileManager.getOrCreateDirectory(imagesDirectoriesPath)
        ?: throw ConfigurationDoesNotExistException()

    private val gallery: File = fileManager.getOrCreateFile(
        imagesDirectoriesPath,
        galleryFileName,
        getDefaultGallery().toString()
    ) ?: throw GalleryImagesDoesNotExistException()

    private fun getDefaultGallery(): JSONObject {
        return JSONObject().apply {
            put("images", JSONArray())
        }
    }

    init {
        try {
            val galleryContent = fileManager.readFile(gallery)
            if (galleryContent != null) {
                if (galleryContent.isNotEmpty()) {
                    val galleryData = JSONObject(galleryContent)
                    val imagesData = galleryData.optJSONArray("images")

                    if (imagesData != null) {
                        for (imageIndex in 0 until imagesData.length()) {
                            try {
                                val jsonObject = imagesData.getJSONObject(imageIndex)
                                imagesList.add(GalleryImage(jsonObject))
                            } catch (e: Exception) {
                                Log.e("GalleryImages", "Error parsing image at index $imageIndex: ${e.message}")
                                // Continue processing other images
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("GalleryImages", "Error initializing gallery: ${e.message}")
            // Initialize with empty gallery
            fileManager.writeFile(gallery, getDefaultGallery().toString())
        }
    }

    fun addImage(galleryImage: GalleryImage, bitmap: Bitmap) {
        Log.d("GalleryImages", "Saving image to local filesystem...")

        val timestamp = "${galleryImage.dateString}_${galleryImage.timeString.replace(":", "-")}"
        val newImageName = "$timestamp.jpg"
        val newImageFile: File = fileManager.getOrCreateFile(imagesDirectoriesPath, newImageName, "")
            ?: throw CouldNotAddImageException()

        // Convert bitmap to JPEG and save directly
        try {
            val outputStream = newImageFile.outputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.flush()
            outputStream.close()

            Log.d("GalleryImages", "Bitmap dimensions: ${bitmap.width}x${bitmap.height}")
            imagesList.add(galleryImage)

            val galleryJSON = createGalleryJSON()
            fileManager.writeFile(gallery, galleryJSON.toString())

            Log.d("GalleryImages", "Image saved successfully with ID: ${galleryImage.scanID}")
            Log.d("GalleryImages", "Saved image to ${newImageFile.absolutePath}")
            Log.d("GalleryImages", "Image size (bytes): ${newImageFile.length()}")
        } catch (e: Exception) {
            Log.e("GalleryImages", "Failed to save image: ${e.message}", e)
            //throw CouldNotAddImageException("Failed to save image: ${e.message}", e)
        }
    }

    private fun createGalleryJSON(): JSONObject {
        val images = JSONArray()
        for (galleryImage in imagesList) {
            images.put(galleryImage.toJSONObject())
        }

        return JSONObject().apply {
            put("images", images)
        }
    }

    fun getImages(): List<GalleryImage> {
        return imagesList
    }
}