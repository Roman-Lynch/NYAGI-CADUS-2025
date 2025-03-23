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
                Language.Arabic,
                Language.BrazilianPortuguese,
                Language.Indonesian,

            ),
            arrayOf(
                "Rotate Screen",
                "Girar pantalla",
                "Faire pivoter l'écran",
                "旋转屏幕",
                "स्क्रीन घुमाएँ",
                "Rodar o ecrã",
                "تدوير الشاشة",
                "Rotação da tela",
                "Putar Layar"
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
                Language.Arabic,
                Language.BrazilianPortuguese,
                Language.Indonesian,
            ),
            arrayOf(
                "No Camera Permissions",
                "Sin permisos de cámara",
                "Aucune autorisation de l'appareil photo",
                "无摄像头权限",
                "कोई कैमरा अनुमति नहीं",
                "Sem permissões para a câmara",
                "لا توجد أذونات للكاميرا",
                "Sem permissões de câmera",
                "Tidak Ada Izin Kamera"
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
                Language.Arabic,
                Language.BrazilianPortuguese,
                Language.Indonesian,
            ),
            arrayOf(
                "You need to enable camera permissions in your device's settings to use this page",
                "Debe habilitar los permisos de la cámara en la configuración de su dispositivo para utilizar esta página",
                "Vous devez activer les autorisations de l'appareil photo dans les paramètres de votre appareil pour utiliser cette page",
                "您需要在设备的设置中启用摄像头权限才能使用此页面",
                "इस पेज का उपयोग करने के लिए आपको अपने डिवाइस की सेटिंग में कैमरा अनुमतियाँ सक्षम करनी होंगी",
                "É necessário ativar as permissões da câmara nas definições do seu dispositivo para utilizar esta página",
                "تحتاج إلى تمكين أذونات الكاميرا في إعدادات جهازك لاستخدام هذه الصفحة",
                "É necessário ativar as permissões da câmera nas configurações do seu dispositivo para usar essa página",
                "Anda harus mengaktifkan izin kamera dalam pengaturan perangkat Anda untuk menggunakan halaman ini"
            )
        )
    }
}