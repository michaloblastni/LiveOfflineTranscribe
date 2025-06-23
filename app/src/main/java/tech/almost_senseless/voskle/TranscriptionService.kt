package tech.almost_senseless.voskle

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import tech.almost_senseless.voskle.data.UserPreferences

class TranscriptionService : Service(), RecognitionListener {

    private var speechService: SpeechService? = null
    private var model: Model? = null
    private var recognizer: Recognizer? = null
    private val channelId = "voskle_transcription"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, createNotification("Listening..."))

        try {
            val prefs = getSharedPreferences("voskle", MODE_PRIVATE)
            val modelPath = prefs.getString("modelPath", null)
            if (modelPath.isNullOrBlank()) {
                Log.e("TranscriptionService", "No model selected! Not starting transcription.")
                stopSelf()
                return
            }

            Log.d("TranscriptionService", "Loading model from $modelPath")
            model = Model(modelPath)
            recognizer = Recognizer(model, 16000.0f)
            speechService = SpeechService(recognizer, 16000.0f)
            speechService?.startListening(this)
            Log.i("TranscriptionService", "Model initialized at $modelPath")
        } catch (e: Exception) {
            Log.e("TranscriptionService", "Model load failed", e)
            stopSelf()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        try {
            speechService?.stop()
            recognizer?.close()
            model?.close()
        } catch (e: Exception) {
            Log.e("TranscriptionService", "onDestroy failed", e)
        }
    }

    override fun onPartialResult(hypothesis: String?) {
    }

    override fun onResult(hypothesis: String?) {
        Log.d("Result from the service", hypothesis ?: "")
        val intent = Intent("TRANSCRIPTION_UPDATE")
        intent.putExtra("transcript", hypothesis ?: "")
        sendBroadcast(intent)
    }

    override fun onFinalResult(hypothesis: String?) {
        Log.d("Final", hypothesis ?: "")
    }

    override fun onError(e: Exception?) {
        Log.e("TranscriptionService", "Error", e)
    }

    override fun onTimeout() {
        Log.w("TranscriptionService", "Timeout")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                channelId,
                "Vosk Transcription Service",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)
                ?.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(text: String): Notification {
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Voskle is transcribing")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .build()
    }
}
