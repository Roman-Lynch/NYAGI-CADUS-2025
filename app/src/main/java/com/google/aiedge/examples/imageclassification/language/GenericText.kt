package com.google.aiedge.examples.imageclassification.language;

class GenericText {
    companion object {
        val ok = TextField(
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
                "ok",
                "ok",
                "ok",
                "确定",
                "ठीक है",
                "ok",
                "حسنًا",
                "ok",
                "baiklah"
            )
        )
    }
}
