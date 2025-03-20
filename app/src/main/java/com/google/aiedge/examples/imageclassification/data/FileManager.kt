import android.content.Context
import java.io.File
import org.json.JSONObject

class FileManager(private val context: Context) {

    fun createDirectory(dirName: String): File? {
        val directory = File("${context.getExternalFilesDir(null)}/$dirName")
        return if (!directory.exists()) {
            if (directory.mkdirs()) directory else null
        } else directory
    }

    fun writeFile(fileName: String, content: String, dir: File) {
        val file = File(dir, fileName)
        file.writeText(content)
    }

    fun readFile(fileName: String, dir: File): String? {
        val file = File(dir, fileName)
        return if (file.exists()) file.readText() else null
    }

    fun writeJson(fileName: String, dir: File, addJsonToJsonObject: (JSONObject) -> Unit) {
        val jsonObject = JSONObject()

        addJsonToJsonObject(jsonObject)

        writeFile(fileName, jsonObject.toString(4), dir)
    }

    fun readJson(fileName: String, dir: File): JSONObject? {
        val jsonString = readFile(fileName, dir)
        return jsonString?.let { JSONObject(it) }
    }
}


