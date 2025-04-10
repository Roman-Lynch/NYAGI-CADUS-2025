import android.content.Context
import android.util.Log
import java.io.File
import org.json.JSONObject
import java.io.FileOutputStream

class FileManager(private val context: Context) {


    fun getOrCreateFile(directoryPath: String, fileName: String, defaultContents: String): File? {

        Log.d("MainActivity", "Attempting to create new file: $fileName")

        val directory = File("${context.getExternalFilesDir(null)}/$directoryPath")
        val file = File(directory, fileName)

        Log.d("MainActivity", "file.exists: ${file.exists()}")

        if (!file.exists()) {
            Log.d("MainActivity", "File does not exist: ${!file.exists()}, so overwriting with default value")
            if (!file.createNewFile()) return null
            writeFile(file, defaultContents)
        }

        return file
    }

    fun getOrCreateDirectory(directoryName: String): File? {
        val directory = File("${context.getExternalFilesDir(null)}/$directoryName")
        return if (!directory.exists()) {
            if (directory.mkdirs()) directory else null
        } else directory
    }

    fun writeFile(file: File, content: String) {
        Log.d("MainActivity", "Saving image to: ${file.absolutePath} with content: $content")
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


