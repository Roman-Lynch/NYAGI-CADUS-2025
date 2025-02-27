package com.google.aiedge.examples.imageclassification

fun toAndroidPath(filePath: String): String {
    return "file:///android_asset/${filePath}"
}