package GuevaraWilmer.cazarpatos

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class InternalFileHandler(private val context: Context) {

    private val fileName = "datos_usuario.txt"

    fun guardarDatos(email: String, password: String) {
        val datos = "$email|$password"
        val writer = OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE))
        writer.write(datos)
        writer.close()
    }

    fun leerDatos(): Pair<String, String> {
        return try {
            val reader = BufferedReader(InputStreamReader(context.openFileInput(fileName)))
            val line = reader.readLine()
            reader.close()
            val partes = line.split("|")
            Pair(partes[0], partes[1])
        } catch (e: Exception) {
            Pair("", "")
        }
    }
}
