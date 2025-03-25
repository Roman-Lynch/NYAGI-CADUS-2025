package com.google.aiedge.examples.imageclassification.language

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

// You can define the supported languages here
enum class Language(val locale: Locale, val flagPath: String, val nativeName: String) {
    ENGLISH(Locale("en"), "Flags/EnglishFlag.png", "English"),
    SPANISH(Locale("es"), "Flags/SpanishFlag.png", "Español"),
    FRENCH(Locale("fr"), "Flags/FrenchFlag.png", "Français"),
    MANDARIN(Locale("zh"), "Flags/MandarinFlag.png", "普通话"),
    HINDI(Locale("hi"), "Flags/HindiFlag.png", "हिन्दी"),
    PORTUGUESE(Locale("pt"), "Flags/PortugueseFlag.png", "Português"),
    ARABIC(Locale("ar"), "Flags/ArabicFlag.png", "لـَهْجة"),
    BRAZILIAN_PORTUGUESE(Locale("pt"), "Flags/BrazilianFlag.png", "Português")
}

class TextField(private val context: Context, private val language: Language) {

    private val localizedContext = applyLocale(context, language.locale)

    private val resources = localizedContext.resources

    // Apply the selected language to context
    private fun applyLocale(context: Context, locale: Locale): Context {
        val supportedLocales = Language.entries.map { it.locale.language }
        val resolvedLocale = if (supportedLocales.contains(locale.language)) locale else Locale("en")

        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(resolvedLocale)
        return context.createConfigurationContext(configuration)
    }

    // Get the localized string using the resources system
    fun get(key: String): String {
        val resourceId = resources.getIdentifier(key, "string", context.packageName)
        return if (resourceId != 0) {
            resources.getString(resourceId)
        } else {
            key // Return the key if not found in resources
        }
    }

    // Optionally, you can provide the flag path if needed
    fun getFlagPath(): String {
        return language.flagPath
    }

    fun getNativeName(): String {
        return language.nativeName
    }
}
