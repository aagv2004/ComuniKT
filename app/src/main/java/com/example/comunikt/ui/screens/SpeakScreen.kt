package com.example.comunikt.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.comunikt.data.MessageRepository

@Composable
fun SpeakScreen(
    onBack: () -> Unit,
) {
    val context = LocalContext.current.applicationContext

    var recognizedText by rememberSaveable {
        mutableStateOf("")
    }

    var notice by rememberSaveable {
        mutableStateOf("Pulsa el botón para comenzar a escuchar.")
    }

    var historyNotice by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var isListening by remember {
        mutableStateOf(false)
    }

    var isSaving by remember {
        mutableStateOf(false)
    }

    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO,
            ) == PackageManager.PERMISSION_GRANTED,
        )
    }

    var recognizer by remember {
        mutableStateOf<SpeechRecognizer?>(null)
    }

    val recognitionAvailable = remember {
        SpeechRecognizer.isRecognitionAvailable(context)
    }

    fun recognitionErrorMessage(error: Int): String {
        return when (error) {
            SpeechRecognizer.ERROR_AUDIO ->
                "No se pudo acceder correctamente al audio."

            SpeechRecognizer.ERROR_CLIENT ->
                "El reconocimiento fue cancelado."

            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->
                "ComuniKT no tiene permiso para utilizar el micrófono."

            SpeechRecognizer.ERROR_NETWORK,
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT ->
                "Hubo un problema de conexión durante el reconocimiento."

            SpeechRecognizer.ERROR_NO_MATCH ->
                "No se pudo reconocer lo que dijiste. Inténtalo nuevamente."

            SpeechRecognizer.ERROR_RECOGNIZER_BUSY ->
                "El reconocimiento de voz está ocupado. Espera un momento e inténtalo nuevamente."

            SpeechRecognizer.ERROR_SERVER ->
                "El servicio de reconocimiento no está disponible en este momento."

            SpeechRecognizer.ERROR_SPEECH_TIMEOUT ->
                "No se detectó voz. Inténtalo nuevamente."

            else ->
                "No se pudo completar el reconocimiento de voz."
        }
    }

    fun startListening() {
        if (!recognitionAvailable) {
            notice =
                "Este dispositivo no tiene disponible un servicio de reconocimiento de voz."
            return
        }

        val activeRecognizer = recognizer

        if (activeRecognizer == null) {
            notice =
                "El reconocimiento de voz todavía no está disponible."
            return
        }

        recognizedText = ""
        historyNotice = null
        notice = "Preparando el micrófono…"

        val intent = Intent(
            RecognizerIntent.ACTION_RECOGNIZE_SPEECH,
        ).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
            )

            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                "es-CL",
            )

            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "es-CL",
            )

            putExtra(
                RecognizerIntent.EXTRA_PARTIAL_RESULTS,
                true,
            )

            putExtra(
                RecognizerIntent.EXTRA_MAX_RESULTS,
                3,
            )
        }

        activeRecognizer.startListening(intent)
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.RequestPermission(),
        ) { granted ->
            hasAudioPermission = granted

            if (granted) {
                notice =
                    "Permiso concedido. Preparando reconocimiento…"

                startListening()
            } else {
                notice =
                    "Se necesita permiso de micrófono para convertir la voz en texto."
            }
        }

    DisposableEffect(context) {
        if (!recognitionAvailable) {
            notice =
                "Este dispositivo no tiene disponible un servicio de reconocimiento de voz."

            onDispose {
                recognizer = null
            }
        } else {
            val speechRecognizer =
                SpeechRecognizer.createSpeechRecognizer(
                    context,
                )

            speechRecognizer.setRecognitionListener(
                object : RecognitionListener {

                    override fun onReadyForSpeech(
                        params: Bundle?,
                    ) {
                        isListening = true
                        notice =
                            "Escuchando… Habla ahora."
                    }

                    override fun onBeginningOfSpeech() {
                        notice =
                            "Detectando voz…"
                    }

                    override fun onRmsChanged(
                        rmsdB: Float,
                    ) {
                        // No necesitamos mostrar el volumen.
                    }

                    override fun onBufferReceived(
                        buffer: ByteArray?,
                    ) {
                        // No necesitamos procesar audio manualmente.
                    }

                    override fun onEndOfSpeech() {
                        isListening = false
                        notice =
                            "Procesando lo que dijiste…"
                    }

                    override fun onError(
                        error: Int,
                    ) {
                        isListening = false
                        notice =
                            recognitionErrorMessage(error)
                    }

                    override fun onResults(
                        results: Bundle?,
                    ) {
                        isListening = false

                        val matches =
                            results?.getStringArrayList(
                                SpeechRecognizer.RESULTS_RECOGNITION,
                            )

                        val finalText =
                            matches
                                ?.firstOrNull()
                                ?.trim()
                                .orEmpty()

                        if (finalText.isEmpty()) {
                            notice =
                                "No se pudo reconocer ningún texto."
                        } else {
                            recognizedText = finalText

                            notice =
                                "Texto reconocido correctamente."
                        }
                    }

                    override fun onPartialResults(
                        partialResults: Bundle?,
                    ) {
                        val matches =
                            partialResults?.getStringArrayList(
                                SpeechRecognizer.RESULTS_RECOGNITION,
                            )

                        val partialText =
                            matches
                                ?.firstOrNull()
                                ?.trim()
                                .orEmpty()

                        if (partialText.isNotEmpty()) {
                            recognizedText =
                                partialText
                        }
                    }

                    override fun onEvent(
                        eventType: Int,
                        params: Bundle?,
                    ) {
                        // No necesitamos eventos adicionales.
                    }
                },
            )

            recognizer = speechRecognizer

            onDispose {
                isListening = false

                speechRecognizer.cancel()
                speechRecognizer.destroy()

                recognizer = null
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState(),
            )
            .padding(24.dp),
        verticalArrangement =
            Arrangement.spacedBy(16.dp),
    ) {
        TextButton(
            onClick = onBack,
        ) {
            Text("Volver")
        }

        Text(
            text = "Hablar",
            style =
                MaterialTheme.typography.headlineLarge,
        )

        Text(
            text =
                "Convierte lo que escuchas en texto para facilitar la comunicación.",
            style =
                MaterialTheme.typography.bodyLarge,
        )

        OutlinedCard(
            modifier =
                Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier =
                    Modifier.padding(20.dp),
                verticalArrangement =
                    Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Texto reconocido",
                    style =
                        MaterialTheme.typography.titleMedium,
                )

                Text(
                    text = if (
                        recognizedText.isBlank()
                    ) {
                        "El texto aparecerá aquí cuando hables."
                    } else {
                        recognizedText
                    },
                    style =
                        MaterialTheme.typography.bodyLarge,
                    color = if (
                        recognizedText.isBlank()
                    ) {
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                    } else {
                        MaterialTheme
                            .colorScheme
                            .onSurface
                    },
                )
            }
        }

        Button(
            enabled =
                recognitionAvailable &&
                        !isListening,
            modifier =
                Modifier.fillMaxWidth(),
            onClick = {
                historyNotice = null

                if (hasAudioPermission) {
                    startListening()
                } else {
                    notice =
                        "Solicitando permiso de micrófono…"

                    permissionLauncher.launch(
                        Manifest.permission.RECORD_AUDIO,
                    )
                }
            },
        ) {
            Text(
                if (isListening) {
                    "Escuchando…"
                } else {
                    "Comenzar a escuchar"
                },
            )
        }

        OutlinedButton(
            enabled = isListening,
            modifier =
                Modifier.fillMaxWidth(),
            onClick = {
                recognizer?.stopListening()

                notice =
                    "Finalizando reconocimiento…"
            },
        ) {
            Text("Detener")
        }

        Text(
            text = notice,
            style =
                MaterialTheme.typography.bodyMedium,
        )

        Button(
            enabled =
                recognizedText.isNotBlank() &&
                        !isListening &&
                        !isSaving,
            modifier =
                Modifier.fillMaxWidth(),
            onClick = {
                isSaving = true
                historyNotice =
                    "Guardando en el historial…"

                MessageRepository
                    .saveSpeechToTextMessage(
                        recognizedText,
                    ) { error ->
                        isSaving = false

                        historyNotice =
                            error
                                ?: "Texto guardado en el historial."
                    }
            },
        ) {
            Text(
                if (isSaving) {
                    "Guardando…"
                } else {
                    "Guardar en historial"
                },
            )
        }

        OutlinedButton(
            enabled =
                recognizedText.isNotBlank() ||
                        isListening,
            modifier =
                Modifier.fillMaxWidth(),
            onClick = {
                if (isListening) {
                    recognizer?.cancel()
                }

                isListening = false
                recognizedText = ""
                historyNotice = null

                notice =
                    "Pulsa el botón para comenzar a escuchar."
            },
        ) {
            Text("Limpiar")
        }

        historyNotice?.let { message ->
            Text(
                text = message,
                style =
                    MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme.primary,
            )
        }
    }
}