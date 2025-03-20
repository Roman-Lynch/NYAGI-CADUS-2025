import android.content.Context
import java.io.File
import org.json.JSONObject

class FileManager(private val context: Context) {


    fun getFileFromExternalStorage(context: Context, directoryPath: String, fileName: String): File {
        val directory = File("${context.getExternalFilesDir(null)}/$directoryPath")
        return File(directory, fileName)
    }

    fun createDirectory(directoryName: String): File? {
        val directory = File("${context.getExternalFilesDir(null)}/$directoryName")
        return if (!directory.exists()) {
            if (directory.mkdirs()) directory else null
        } else directory
    }

    fun writeFile(file: File, content: String) {
        file.writeText(content)
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


