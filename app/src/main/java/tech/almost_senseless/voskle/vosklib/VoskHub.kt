package tech.almost_senseless.voskle.vosklib

import android.content.Context
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlinx.serialization.json.Json
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.SpeakerModel
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import tech.almost_senseless.voskle.ErrorKind
import tech.almost_senseless.voskle.VLTAction
import tech.almost_senseless.voskle.VLTViewModel
import tech.almost_senseless.voskle.data.Languages
import tech.almost_senseless.voskle.R
import java.io.File
import java.io.IOException
import java.util.concurrent.Executors
import java.util.function.Consumer

private const val TAG = "VoskHub"
const val SPEAKER_MODEL_PATH = "vosk-model-spk-0.4"

class VoskHub (
    private val context: Context
) : RecognitionListener {
    private var viewModel: VLTViewModel? = null
    private var onAction: ((VLTAction) -> Unit)? = null
    private var speechService: SpeechService? = null
    private var modelPath: String? = null
    private var model: Model? = null
    private var speakerModel: SpeakerModel? = null

    private val json = Json { encodeDefaults = true }

    fun subscribeToViewModel(viewModel: VLTViewModel): Boolean {
        if (this.viewModel == null || this.viewModel!! !== viewModel) {
            this.viewModel = viewModel
            this.onAction = viewModel::onAction
            updateApplicationState(VLTAction.RegisterVoskHub(this))
        }
        return this.viewModel != null
    }

    private fun updateApplicationState(action: VLTAction): Boolean {
        return if (this.viewModel != null && this.onAction != null) {
            this.onAction!!(action)
            true
        } else {
            false
        }
    }

    fun setModelPath(modelPath: String) {
        this.modelPath = modelPath
    }

    fun getModelPath(): String? = this.modelPath

    fun initModel(withSpeakerModel: Boolean = false): Boolean {
        if (this.model != null) return true
        if (this.modelPath != null && isModelAvailable()) {
            updateApplicationState(VLTAction.ShowDownloadConfirmation(false))
            val currentLanguage = modelPath ?: Languages.ENGLISH_US.modelPath
            getModel(currentLanguage,
                { model: Model? ->
                    if (withSpeakerModel && isSpeakerModelAvailable()) {
                        try {
                            val externalFilesDir = context.getExternalFilesDir(null)
                                ?: throw IOException(context.getString(R.string.external_files_dir_error, Environment.getExternalStorageState()))
                            speakerModel = SpeakerModel("$externalFilesDir/models/$SPEAKER_MODEL_PATH")
                        } catch (e: IOException) {
                            onError(e)
                        }
                    }
                    this.model = model
                    updateApplicationState(VLTAction.SetModelStatus(true))
                },
                { exception: IOException -> onError(exception) })
        }
        return this.model != null
    }

    private fun getModel(modelPath: String, completeCallback: Consumer<Model>, errorCallback: Consumer<IOException>) {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            try {
                val externalFilesDir = context.getExternalFilesDir(null)
                    ?: throw IOException(context.getString(R.string.external_files_dir_error, Environment.getExternalStorageState()))
                val model = Model("$externalFilesDir/models/$modelPath")
                Log.d(TAG, "getModel: Model created successfully.")
                handler.post { completeCallback.accept(model) }
            } catch (e: IOException) {
                Log.e(TAG, "getModel: ERROR ${e.message}")
                handler.post { errorCallback.accept(e) }
            }
        }
    }

    fun isModelAvailable(): Boolean {
        val externalFilesDir = context.getExternalFilesDir(null)
        val pathToModel = modelPath ?: Languages.ENGLISH_US.modelPath
        val dir = File("$externalFilesDir/models/$pathToModel")
        return dir.exists() && dir.isDirectory
    }

    fun isSpeakerModelAvailable(): Boolean {
        val externalFilesDir = context.getExternalFilesDir(null)
        val dir = File("$externalFilesDir/models/$SPEAKER_MODEL_PATH")
        return dir.exists() && dir.isDirectory
    }

    fun toggleRecording() {
        Log.w(TAG, "toggleRecording() is deprecated. TranscriptionService handles recording now.")
    }

    override fun onPartialResult(hypothesis: String?) {
        Log.d(TAG, "onPartialResult: $hypothesis")
        val partialResult: PartialResult = json.decodeFromString(hypothesis ?: return)
        if (partialResult.partial.isNotEmpty())
            updateApplicationState(VLTAction.UpdateLastResult(partialResult.partial))
    }

    override fun onResult(hypothesis: String?) {
        val result: Result = json.decodeFromString(hypothesis ?: return)
        Log.d(TAG, "onResult: $result")
        if (result.text.isNotEmpty()) {
            speakerModel?.let {
                updateApplicationState(VLTAction.ProcessSpeakerInfo(result.speakerFingerprint, result.speakerDataLength))
            }
            updateApplicationState(VLTAction.UpdateTranscript(result.text))
        }
    }

    override fun onFinalResult(hypothesis: String?) {
        val result: Result = json.decodeFromString(hypothesis ?: return)
        if (result.text.isNotEmpty())
            updateApplicationState(VLTAction.UpdateTranscript(result.text))
    }

    override fun onError(exception: Exception?) {
        updateApplicationState(VLTAction.SetError(ErrorKind.ModelError(exception?.localizedMessage ?: "")))
    }

    override fun onTimeout() {
        updateApplicationState(VLTAction.SetError(ErrorKind.TranscriptionTimeout))
    }

    fun isUsingSpeakerRecognition(): Boolean = speakerModel != null

    fun reset() {
        speechService?.stop()
        speechService?.shutdown()
        speechService = null
        speakerModel = null
        model = null
        updateApplicationState(VLTAction.SetModelStatus(false))
    }
}
