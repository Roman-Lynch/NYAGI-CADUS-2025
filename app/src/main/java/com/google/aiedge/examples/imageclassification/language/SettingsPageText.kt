package com.google.aiedge.examples.imageclassification.language

class SettingsPageText {
    companion object {
        val title = TextField(
            arrayOf(
                Language.English,
                Language.Spanish,
                Language.French,
                Language.Mandarin,
                Language.Hindi,
                Language.Portuguese,
                Language.Arabic,
                Language.BrazilianPortuguese,
                Language.Indonesian,
            ),
            arrayOf(
                "Settings",
                "Ajustes",
                "Paramètres",
                "设置",
                "सेटिंग्स",
                "Configurações",
                "إعدادات",
                "Configurações",
                "Pengaturan"
            )
        )

        val language = TextField(
            arrayOf(
                Language.English,
                Language.Spanish,
                Language.French,
                Language.Mandarin,
                Language.Hindi,
                Language.Portuguese,
                Language.Arabic,
                Language.BrazilianPortuguese,
                Language.Indonesian,
            ),
            arrayOf(
                "Language",
                "Idioma",
                "Langue",
                "语言",
                "भाषा",
                "Linguagem",
                "لغة",
                "Idioma",
                "Bahasa"
            )
        )
    }
}