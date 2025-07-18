package GuevaraWilmer.cazarpatos

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
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

    // Declarar la instancia de FirebaseAuth
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicialización de variables
        manejadorArchivo = ExternalFileManager(this)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        buttonNewUser = findViewById(R.id.buttonNewUser)
        checkBoxRecordarme = findViewById(R.id.checkBoxRecordarme)

        // Inicializar FirebaseAuth
        auth = Firebase.auth

        // Leer preferencias guardadas
        LeerDatosDePreferencias()

        // Evento para el botón de login
        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            // Validación de los datos requeridos
            if (!ValidarDatosRequeridos()) {
                return@setOnClickListener
            }

            // Llamada a la función para autenticar al usuario
            authenticateUser(email, password)
        }

        // Evento para el botón de nuevo usuario (redirige a RegisterActivity)
        buttonNewUser.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)  // Redirige a la pantalla de registro
            startActivity(intent)
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

    private fun GuardarDatosEnPreferencias(email: String, clave: String) {
        val listadoAGrabar: Pair<String, String> = if (checkBoxRecordarme.isChecked) {
            email to clave
        } else {
            "" to ""
        }

        manejadorArchivo.SaveInformation(listadoAGrabar)
    }

    private fun ValidarDatosRequeridos(): Boolean {
        val email = editTextEmail.text.toString()
        val clave = editTextPassword.text.toString()

        if (email.isBlank() || clave.isBlank()) {
            Toast.makeText(this, "Por favor, ingresa tu correo y contraseña", Toast.LENGTH_SHORT).show()
            return false
        }

        // Agregar validación de formato de correo si es necesario
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Por favor, ingresa un correo electrónico válido", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    // Función para autenticar al usuario con Firebase
    fun authenticateUser(email: String, password: String) {
        // Log para verificar que se está intentando realizar la autenticación
        Log.d(EXTRA_LOGIN, "Attempting to sign in with email: $email")

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Si el login es exitoso, navegar a la pantalla principal
                    Log.d(EXTRA_LOGIN, "signInWithEmail:success")  // Este log se muestra cuando el login es exitoso
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra(EXTRA_LOGIN, auth.currentUser!!.email)
                    startActivity(intent)
                    finish() // Para cerrar la actividad de login
                } else {
                    // Si el login falla, mostrar el error
                    Log.w(EXTRA_LOGIN, "signInWithEmail:failure", task.exception)  // Este log se muestra cuando el login falla
                    Toast.makeText(baseContext, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }
}
