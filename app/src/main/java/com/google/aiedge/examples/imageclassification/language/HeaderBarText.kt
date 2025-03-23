package com.google.aiedge.examples.imageclassification.language

class HeaderBarText {
    companion object {
        val backButtonLabel = TextField(
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
                "Go Back",
                "Volver",
                "Retourner",
                "回去",
                "वापस जाओ",
                "Volte",
                "عُد",
                "Voltar",
                "Kembali"
            )
        )
    }
}