package com.google.aiedge.examples.imageclassification.language

import android.content.Context
import android.util.Log
import com.google.aiedge.examples.imageclassification.data.Configurations


class LanguageSettingsGateway(private val context: Context) {

    private val configurations: Configurations = Configurations(context)

    fun getSavedLanguage(): Language {

        fun isValidValue(value: String): Boolean {
            return Language.entries.map { language -> language.nativeName }.contains(value)
        }

        val languageName = configurations.getFromConfigurations("language", ::isValidValue)

        return Language.entries.find { it.nativeName == languageName } ?: Language.English
    }

    fun setSavedLanguage(language: Language) {

        Log.d("MainActivity", "Attempting to set saved language ${language.nativeName}")

        configurations.putToConfigurations("language", language.nativeName)
    }
}