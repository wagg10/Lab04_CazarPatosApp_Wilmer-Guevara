package GuevaraWilmer.cazarpatos

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AlertDialog
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_LOGIN = "com.epnfis.cazarpatos.EXTRA_LOGIN"
    }

    private lateinit var textViewUser: TextView
    private lateinit var textViewCounter: TextView
    private lateinit var textViewTime: TextView
    private lateinit var imageViewDuck: ImageView
    private lateinit var soundPool: SoundPool
    private val handler = Handler(Looper.getMainLooper())  // Manejador para retrasar la restauración de la imagen
    private var counter = 0
    private var screenWidth = 0
    private var screenHeight = 0
    private var soundId: Int = 0
    private var isLoaded = false
    private var gameOver = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Inicialización de variables
        textViewUser = findViewById(R.id.textViewUser)
        textViewCounter = findViewById(R.id.textViewCounter)
        textViewTime = findViewById(R.id.textViewTime)
        imageViewDuck = findViewById(R.id.imageViewDuck)

        // Obtener el usuario de la pantalla LoginActivity
        val extras = intent.extras ?: return
        val usuario = extras.getString(EXTRA_LOGIN) ?: "Unknown"
        textViewUser.setText(usuario)

        // Inicializar la pantalla
        initializeScreen()
        // Iniciar cuenta regresiva del juego
        initializeCountdown()

        // Configuración de SoundPool para los efectos de sonido
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setAudioAttributes(audioAttributes)
            .setMaxStreams(10)  // Puedes reproducir hasta 10 sonidos a la vez
            .build()

        // Cargar el sonido de disparo
        soundId = soundPool.load(this, R.raw.gunshot, 1)

        // Listener cuando el sonido está cargado
        soundPool.setOnLoadCompleteListener { _, _, _ ->
            isLoaded = true
        }

        // Cuando se hace clic en el pato
        imageViewDuck.setOnClickListener {
            if (gameOver) return@setOnClickListener
            counter++
            if (isLoaded) {
                soundPool.play(soundId, 1f, 1f, 0, 0, 1f)  // Reproducir sonido de disparo
            }
            textViewCounter.setText(counter.toString())
            imageViewDuck.setImageResource(R.drawable.duck_clicked)

            // Restaurar la imagen del pato después de 500ms
            handler.postDelayed({
                imageViewDuck.setImageResource(R.drawable.duck)
            }, 500)

            // Mover el pato a una posición aleatoria
            moveDuckRandomly()
        }
    }

    // Inicializar el tamaño de la pantalla
    private fun initializeScreen() {
        val display = this.resources.displayMetrics
        screenWidth = display.widthPixels
        screenHeight = display.heightPixels
    }

    // Mover el pato a una posición aleatoria
    private fun moveDuckRandomly() {
        val min = imageViewDuck.width / 2
        val maxX = screenWidth - imageViewDuck.width
        val maxY = screenHeight - imageViewDuck.height

        val randomX = Random.nextInt(0, maxX - min + 1)
        val randomY = Random.nextInt(0, maxY - min + 1)

        imageViewDuck.animate()
            .x(randomX.toFloat())
            .y(randomY.toFloat())
            .setDuration(300)  // Duración de la animación
            .start()
    }

    // Configuración de la cuenta regresiva
    private var countDownTimer = object : CountDownTimer(10000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val secondsRemaining = millisUntilFinished / 1000
            textViewTime.setText("${secondsRemaining}s")
        }

        override fun onFinish() {
            textViewTime.setText("0s")
            gameOver = true
            showGameOverDialog()
        }
    }

    // Iniciar cuenta regresiva
    private fun initializeCountdown() {
        countDownTimer.start()
    }

    // Mostrar el diálogo de fin de juego
    private fun showGameOverDialog() {
        val builder = AlertDialog.Builder(this)
        builder
            .setMessage(getString(R.string.dialog_message_congratulations, counter))
            .setTitle(getString(R.string.dialog_title_game_end))
            .setPositiveButton(getString(R.string.button_restart)) { _, _ ->
                restartGame()
            }
            .setNegativeButton(getString(R.string.button_close)) { _, _ -> }
            .setCancelable(false)  // Previene que el diálogo se cierre tocando fuera de él
        builder.create().show()
    }

    // Reiniciar el juego
    private fun restartGame() {
        counter = 0
        gameOver = false
        countDownTimer.cancel()
        textViewCounter.setText(counter.toString())
        moveDuckRandomly()
        initializeCountdown()
    }

    // Detener el juego y liberar recursos
    override fun onStop() {
        Log.w(EXTRA_LOGIN, "Play canceled")
        countDownTimer.cancel()
        textViewTime.text = "0s"
        gameOver = true
        soundPool.stop(soundId)
        super.onStop()
    }

    // Liberar recursos cuando la actividad se destruye
    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }
}
