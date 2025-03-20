package com.google.aiedge.examples.imageclassification.data;

import FileManager
import android.content.Context
import org.json.JSONObject
import java.io.File

class Configurations(context: Context) {

    val fileManager: FileManager = FileManager(context)
    private val configurations: File = File(context.filesDir.absolutePath + "/configurations.json")

    init {
        if (!configurations.exists()) {
            configurations.createNewFile()

            fileManager.writeJson(configurations, getDefaultConfigurations())
        }
    }

    private fun getDefaultConfigurations(): JSONObject {

        val defaultConfigurations: JSONObject = JSONObject().apply {
            put("language", "English")
        }

        return defaultConfigurations
    }

    fun getFromConfigurations(key: String, isValid: (String) -> Boolean): String {
        val configurationsJSON: JSONObject = fileManager.readJson(configurations) ?: throw ConfigurationDoesNotExistException()

        val value: String = configurationsJSON.getString(key)
        if (!isValid(value)) throw InvalidConfigurationException()

        return value
    }

    fun putToConfigurations(key: String, value: String) {

        val configurationsJSON: JSONObject = fileManager.readJson(configurations) ?: return
        configurationsJSON.put(key, value)

        fileManager.writeJson(configurations, configurationsJSON)
    }
}

class ConfigurationDoesNotExistException : Exception("Configuration does not exist when fetching from NYAGI configurations.json file")
class InvalidConfigurationException : Exception("The value for a configuration is not in set of valid values")