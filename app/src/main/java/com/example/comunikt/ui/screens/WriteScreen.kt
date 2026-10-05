package com.example.comunikt.ui.screens

import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.example.comunikt.data.MessageRepository
import java.util.Locale
import java.util.UUID

@Composable
fun WriteScreen(onBack: () -> Unit) {
    val context = LocalContext.current.applicationContext

    var message by rememberSaveable {
        mutableStateOf("")
    }

    var notice by remember {
        mutableStateOf("Preparando la voz…")
    }

    var historyNotice by remember {
        mutableStateOf<String?>(null)
    }

    var ready by remember {
        mutableStateOf(false)
    }

    var engine by remember {
        mutableStateOf<TextToSpeech?>(null)
    }

    var currentId by remember {
        mutableStateOf<String?>(null)
    }

    DisposableEffect(context) {
        val handler = Handler(Looper.getMainLooper())

        var disposed = false
        var tts: TextToSpeech? = null

        fun updateNotice(
            id: String?,
            text: String,
        ) {
            handler.post {
                if (!disposed && id == currentId) {
                    notice = text
                }
            }
        }

        tts = TextToSpeech(context) { status ->
            handler.post {
                if (!disposed) {
                    val activeEngine = tts

                    if (
                        status == TextToSpeech.SUCCESS &&
                        activeEngine != null
                    ) {
                        var languageResult =
                            activeEngine.setLanguage(
                                Locale("es", "CL"),
                            )

                        if (
                            languageResult ==
                            TextToSpeech.LANG_MISSING_DATA ||
                            languageResult ==
                            TextToSpeech.LANG_NOT_SUPPORTED
                        ) {
                            languageResult =
                                activeEngine.setLanguage(
                                    Locale("es"),
                                )
                        }

                        ready =
                            languageResult >=
                                    TextToSpeech.LANG_AVAILABLE

                        notice = if (ready) {
                            "Voz lista. Escribe un mensaje."
                        } else {
                            "No hay una voz en español disponible. " +
                                    "Revisa la configuración de texto a voz del teléfono."
                        }
                    } else {
                        notice =
                            "No se pudo iniciar el motor de voz."
                    }
                }
            }
        }

        tts?.setOnUtteranceProgressListener(
            object : UtteranceProgressListener() {

                override fun onStart(
                    utteranceId: String?,
                ) {
                    updateNotice(
                        utteranceId,
                        "Leyendo el mensaje…",
                    )
                }

                override fun onDone(
                    utteranceId: String?,
                ) {
                    updateNotice(
                        utteranceId,
                        "Lectura finalizada.",
                    )
                }

                @Deprecated(
                    "Callback requerido por Android",
                )
                override fun onError(
                    utteranceId: String?,
                ) {
                    updateNotice(
                        utteranceId,
                        "No se pudo reproducir el mensaje.",
                    )
                }
            },
        )

        engine = tts

        onDispose {
            disposed = true
            handler.removeCallbacksAndMessages(null)
            tts?.stop()
            tts?.shutdown()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
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
            text = "Escribir",
            style =
                MaterialTheme.typography.headlineLarge,
        )

        Text(
            "Escribe un mensaje para escucharlo en voz alta.",
        )

        OutlinedTextField(
            value = message,
            onValueChange = {
                message = it
                historyNotice = null
            },
            label = {
                Text("Mensaje")
            },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4,
        )

        Button(
            enabled = ready,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val text = message.trim()

                when {
                    text.isEmpty() -> {
                        notice =
                            "Escribe un mensaje antes de reproducirlo."
                        historyNotice = null
                    }

                    text.length >
                            TextToSpeech
                                .getMaxSpeechInputLength() -> {
                        notice =
                            "El mensaje es demasiado largo. " +
                                    "El máximo es de " +
                                    "${TextToSpeech.getMaxSpeechInputLength()} caracteres."

                        historyNotice = null
                    }

                    else -> {
                        val id =
                            UUID.randomUUID().toString()

                        currentId = id
                        notice =
                            "Preparando la lectura…"

                        historyNotice = null

                        val result = engine?.speak(
                            text,
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            id,
                        )

                        if (
                            result ==
                            TextToSpeech.SUCCESS
                        ) {
                            historyNotice =
                                "Guardando en el historial…"

                            MessageRepository
                                .saveTextToSpeechMessage(
                                    text,
                                ) { error ->
                                    historyNotice =
                                        error
                                            ?: "Mensaje guardado en el historial."
                                }
                        } else {
                            currentId = null

                            notice =
                                "No se pudo reproducir el mensaje."

                            historyNotice = null
                        }
                    }
                }
            },
        ) {
            Text("Reproducir")
        }

        OutlinedButton(
            enabled = ready,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                currentId = null
                engine?.stop()
                notice = "Lectura detenida."
            },
        ) {
            Text("Detener")
        }

        Text(
            text = notice,
            style =
                MaterialTheme.typography.bodyMedium,
        )

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