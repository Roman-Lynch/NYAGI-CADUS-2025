package com.google.aiedge.examples.imageclassification.data;

import FileManager
import android.content.Context
import android.util.Log
import androidx.camera.core.ImageProxy
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class GalleryImagesDoesNotExistException : Exception("galleryImages does not exist when fetching from NYAGI gallery_images directory")
class CouldNotAddImageException: Exception("galleryImages could not add image to gallery_image file")

class GalleryImages(val context: Context) {

    private val imagesDirectoriesPath = "gallery_images"
    private val galleryFileName = "gallery.json"

    private val fileManager: FileManager = FileManager(context)

    private val imagesDirectory: File = fileManager.getOrCreateDirectory(imagesDirectoriesPath) ?: throw ConfigurationDoesNotExistException()
    private val gallery: File = fileManager.getOrCreateFile(imagesDirectoriesPath, galleryFileName, getDefaultGallery().toString()) ?: throw GalleryImagesDoesNotExistException()

    private val imagesList = ArrayList<GalleryImage>()

    private fun getDefaultGallery(): JSONObject {

        return JSONObject().apply {
            put("images", JSONArray())
        }
    }

    init {

        val galleryData: JSONObject = fileManager.readJson(gallery) ?: throw GalleryImagesDoesNotExistException()
        val imagesData: JSONArray = galleryData.getJSONArray("images")

        for (imageIndex in 0 until imagesData.length()) {
            val jsonObject = imagesData.getJSONObject(imageIndex)
            imagesList.add(GalleryImage(jsonObject))
        }
    }

    fun addImage(galleryImage: GalleryImage, imageProxy: ImageProxy) {

        Log.d("Main Activity", "Saved image to local filesystem")

        val newImageName = galleryImage.scanID.toString()
        val newImageFile: File = fileManager.getOrCreateFile(imagesDirectoriesPath, newImageName, "") ?: throw CouldNotAddImageException()

        fun handleByteArrayData(bytes: ByteArray) {
            fileManager.writeFile(newImageFile, bytes)
        }

        ImageConverter.useByteArrayData(imageProxy, ::handleByteArrayData)

        imagesList.add(galleryImage)

        val galleryJSON: JSONObject = createGalleryJSON()
        fileManager.writeFile(gallery, galleryJSON.toString())
    }

    private fun createGalleryJSON(): JSONObject {

        val images = JSONArray()
        for (galleryImage in imagesList) {
            images.put(galleryImage.toJSONObject())
        }

        val galleryJSON: JSONObject = JSONObject().apply {
            put("images", images)
        }

        return galleryJSON
    }

    fun getImages(): List<GalleryImage> {
        return imagesList
    }
}
