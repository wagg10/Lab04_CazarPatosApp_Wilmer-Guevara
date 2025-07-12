package GuevaraWilmer.cazarpatos

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var manejadorArchivo: FileHandler
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonNewUser: Button
    private lateinit var checkBoxRecordarme: CheckBox
    private lateinit var mediaPlayer: MediaPlayer

    companion object {
        const val EXTRA_LOGIN = "extra_login"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicialización de variables
        //manejadorArchivo = SharedPreferencesManager(this)
        //manejadorArchivo = EncryptedSharedPreferencesManager(this)
        //manejadorArchivo = InternalFileManager(this)
        manejadorArchivo = ExternalFileManager(this)

        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        buttonNewUser = findViewById(R.id.buttonNewUser)
        checkBoxRecordarme = findViewById(R.id.checkBoxRecordarme)

        // Leer preferencias guardadas
        LeerDatosDePreferencias()

        // Configurar botón Login
        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString()
            val clave = editTextPassword.text.toString()

            // Validaciones (asumiendo que ValidarDatosRequeridos() existe)
            if (!ValidarDatosRequeridos()) return@setOnClickListener

            // Guardar datos en preferencias si corresponde
            GuardarDatosEnPreferencias()

            // Ir a la pantalla principal
            val intencion = Intent(this, MainActivity::class.java)
            intencion.putExtra(EXTRA_LOGIN, email)
            startActivity(intencion)
        }

        // Botón de nuevo usuario (a definir)
        buttonNewUser.setOnClickListener {
            // Implementar acción si se desea
        }

        // Reproducir sonido
        mediaPlayer = MediaPlayer.create(this, R.raw.title_screen)
        mediaPlayer.start()
    }

    private fun LeerDatosDePreferencias() {
        val listadoLeido = manejadorArchivo.ReadInformation()

        if (listadoLeido.first.isNotEmpty()) {
            checkBoxRecordarme.isChecked = true
        }

        editTextEmail.setText(listadoLeido.first)
        editTextPassword.setText(listadoLeido.second)
    }

    private fun GuardarDatosEnPreferencias() {
        val email = editTextEmail.text.toString()
        val clave = editTextPassword.text.toString()

        val listadoAGrabar: Pair<String, String> = if (checkBoxRecordarme.isChecked) {
            email to clave
        } else {
            "" to ""
        }

        manejadorArchivo.SaveInformation(listadoAGrabar)
    }

    // Este método debe existir. Aquí un ejemplo simple
    private fun ValidarDatosRequeridos(): Boolean {
        val email = editTextEmail.text.toString()
        val clave = editTextPassword.text.toString()

        if (email.isBlank() || clave.isBlank()) {
            Toast.makeText(this, "Por favor, ingresa tu correo y contraseña", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}
