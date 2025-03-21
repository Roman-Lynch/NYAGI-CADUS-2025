package com.google.aiedge.examples.imageclassification.data;

import FileManager
import android.content.Context
import com.google.aiedge.examples.imageclassification.language.Language
import org.json.JSONObject
import java.io.File

class ConfigurationDoesNotExistException : Exception("Configuration does not exist when fetching from NYAGI configurations.json file")
class InvalidConfigurationException : Exception("The value for a configuration is not in set of valid values")

class Configurations(private val context: Context) {
    private val fileManager: FileManager = FileManager(context)

    private val configurations: File = fileManager.getOrCreateFile("", "configurations.json", getDefaultConfigurations().toString()) ?: throw ConfigurationDoesNotExistException()

    init {
        fileManager.writeJson(configurations, getDefaultConfigurations())
    }

    private fun getDefaultConfigurations(): JSONObject {

        return JSONObject().apply {
            put("language", Language.English.nativeName)
        }
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
