package GuevaraWilmer.cazarpatos

import android.app.Activity
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException

class ExternalFileManager(private val actividad: Activity) : FileHandler {

    private val fileName = "external_user_data.txt"

    override fun SaveInformation(datosAGrabar: Pair<String, String>) {
        try {
            val file = File(actividad.getExternalFilesDir(null), fileName)
            val contenido = "${datosAGrabar.first}\n${datosAGrabar.second}"
            val output = FileOutputStream(file)
            output.write(contenido.toByteArray())
            output.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun ReadInformation(): Pair<String, String> {
        try {
            val file = File(actividad.getExternalFilesDir(null), fileName)
            if (!file.exists()) return "" to ""

            val reader = BufferedReader(FileReader(file))
            val email = reader.readLine() ?: ""
            val password = reader.readLine() ?: ""
            reader.close()
            return email to password
        } catch (e: IOException) {
            e.printStackTrace()
            return "" to ""
        }
    }
}