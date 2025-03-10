package com.google.aiedge.examples.imageclassification.language

class CameraText {
    companion object {
        val rotateScreen = TextField(
            arrayOf(
                Language.English,
                Language.Spanish,
                Language.French,
                Language.Mandarin,
                Language.Hindi,
                Language.Portuguese,
                Language.Arabic
            ),
            arrayOf(
                "Rotate Screen",
                "Girar pantalla",
                "Faire pivoter l'écran",
                "旋转屏幕",
                "स्क्रीन घुमाएँ",
                "Girar ecrã",
                "تدوير الشاشة"
            )
        )
    }
}