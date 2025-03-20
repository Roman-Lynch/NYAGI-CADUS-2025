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

        val noPermissionsTitle = TextField(
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
                "No Camera Permissions",
                "Sin permisos de cámara",
                "Aucune autorisation de caméra",
                "无相机权限",
                "कोई कैमरा अनुमति नहीं",
                "Sem permissões de câmara",
                "لا توجد أذونات للكاميرا"
            )
        )

        val noPermissionsBody = TextField(
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
                "You need to enable camera permissions in your device's settings to use this page",
                "Debe habilitar los permisos de la cámara en la configuración de su dispositivo para usar esta página",
                "Vous devez activer les autorisations de l'appareil photo dans les paramètres de votre appareil pour utiliser cette page",
                "您需要在设备设置中启用相机权限才能使用此页面",
                "इस पेज का उपयोग करने के लिए आपको अपने डिवाइस की सेटिंग में कैमरा अनुमतियाँ सक्षम करनी होंगी",
                "Necessita de ativar as permissões da câmara nas definições do seu dispositivo para utilizar esta página",
                "يجب عليك تمكين أذونات الكاميرا في إعدادات جهازك لاستخدام هذه الصفحة"
            )
        )
    }
}