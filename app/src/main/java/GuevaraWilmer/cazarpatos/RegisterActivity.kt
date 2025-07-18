package GuevaraWilmer.cazarpatos

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.content.Intent

class RegisterActivity : AppCompatActivity() {

    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonRegister: Button

    private lateinit var auth: FirebaseAuth

    companion object {
        const val EXTRA_REGISTER = "extra_register"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicializar FirebaseAuth
        auth = Firebase.auth

        // Inicialización de variables
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonRegister = findViewById(R.id.buttonRegister)

        // Configurar el evento del botón de registro
        buttonRegister.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            // Validar los datos
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Por favor ingresa el correo y la contraseña", Toast.LENGTH_SHORT).show()
            } else {
                // Llamar a la función de registro
                SignUpNewUser(email, password)
            }
        }
    }

    // Función para registrar un nuevo usuario
    fun SignUpNewUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Si el registro es exitoso, mostrar mensaje y navegar
                    Log.d(EXTRA_REGISTER, "createUserWithEmail:success")
                    val user = auth.currentUser
                    Toast.makeText(baseContext, "Nuevo usuario registrado.", Toast.LENGTH_SHORT).show()

                    // Redirigir al usuario a la pantalla de login o al inicio
                    val intent = Intent(this, MainActivity::class.java) // O al LoginActivity si lo prefieres
                    startActivity(intent)
                    finish()
                } else {
                    // Si el registro falla, mostrar error
                    Log.w(EXTRA_REGISTER, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Error al registrar el usuario.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
