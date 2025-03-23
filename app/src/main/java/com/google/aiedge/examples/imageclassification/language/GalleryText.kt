package com.google.aiedge.examples.imageclassification.language

class GalleryText {
    companion object {
        val gallery = TextField(
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
                "Gallery",
                "Galería",
                "Galerie",
                "画廊",
                "गैलरी",
                "Galeria",
                "معرض الصور",
                "Galeria",
                "Galeri"
            )
        )
    }
}