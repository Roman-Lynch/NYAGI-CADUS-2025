package com.google.aiedge.examples.imageclassification.language

enum class Language(val nativeName: String, val flagPath: String) {
    English("English", "Flags/EnglishFlag.png"),
    Spanish("Español", "Flags/SpanishFlag.png"),
    French("Français", "Flags/FrenchFlag.png"),
    Mandarin("普通话", "Flags/MandarinFlag.png"),
    Hindi("हिन्दी", "Flags/HindiFlag.png"),
    Portuguese("Português", "Flags/PortugueseFlag.png"),
    Arabic("لـَهْجة", "Flags/ArabicFlag.png"),
    BrazilianPortuguese("Português", "Flags/BrazilianFlag.png"),
    Indonesian("Indonésio", "Flags/IndonesianFlag.png")
}

class TextField(keys: Array<Language>, values: Array<String>) {
    private val languages: Map<Language, String> = keys.zip(values).toMap()

    fun get(key: Language): String {
        return languages[key] ?: key.flagPath
    }
}