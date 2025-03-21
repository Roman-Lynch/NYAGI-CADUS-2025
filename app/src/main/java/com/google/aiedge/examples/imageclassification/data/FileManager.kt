import android.content.Context
import java.io.File
import org.json.JSONObject
import java.io.FileOutputStream

class FileManager(private val context: Context) {


    fun getOrCreateFile(directoryPath: String, fileName: String, defaultContents: String): File? {
        val directory = File("${context.getExternalFilesDir(null)}/$directoryPath")
        val file = File(directory, fileName)

        if (!file.exists()) {
            if (file.createNewFile()) return null
        }

        writeFile(file, defaultContents)
        return file
    }

    fun getOrCreateDirectory(directoryName: String): File? {
        val directory = File("${context.getExternalFilesDir(null)}/$directoryName")
        return if (!directory.exists()) {
            if (directory.mkdirs()) directory else null
        } else directory
    }

    fun writeFile(file: File, content: String) {
        file.writeText(content)
    }

    fun writeFile(file: File, content: ByteArray) {
        FileOutputStream(file).use { fos ->
            fos.write(content)
        }
    }

    fun readFile(file: File): String? {
        return if (file.exists()) file.readText() else null
    }

    fun writeJson(file: File, jsonObject: JSONObject) {

        writeFile(file, jsonObject.toString(4))
    }

    fun readJson(file: File): JSONObject? {
        val jsonString = readFile(file)
        return jsonString?.let { JSONObject(it) }
    }
}


