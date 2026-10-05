package com.example.comunikt.data

import com.example.comunikt.model.HistoryMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source

object MessageRepository {

    private const val TYPE_TEXT_TO_SPEECH =
        "Texto a voz"

    private const val TYPE_SPEECH_TO_TEXT =
        "Voz a texto"

    private const val MAX_MESSAGE_LENGTH =
        4000

    private val auth: FirebaseAuth
        get() = FirebaseAuth.getInstance()

    private val firestore: FirebaseFirestore
        get() = FirebaseFirestore.getInstance()

    fun saveTextToSpeechMessage(
        text: String,
        onResult: (String?) -> Unit,
    ) {
        saveMessage(
            text = text,
            type = TYPE_TEXT_TO_SPEECH,
            onResult = onResult,
        )
    }

    fun saveSpeechToTextMessage(
        text: String,
        onResult: (String?) -> Unit,
    ) {
        saveMessage(
            text = text,
            type = TYPE_SPEECH_TO_TEXT,
            onResult = onResult,
        )
    }

    private fun saveMessage(
        text: String,
        type: String,
        onResult: (String?) -> Unit,
    ) {
        val normalizedText =
            text.trim()

        if (normalizedText.isEmpty()) {
            onResult(
                "No se puede guardar un mensaje vacío.",
            )
            return
        }

        if (
            normalizedText.length >
            MAX_MESSAGE_LENGTH
        ) {
            onResult(
                "El mensaje supera el límite de $MAX_MESSAGE_LENGTH caracteres.",
            )
            return
        }

        val currentUser =
            auth.currentUser

        if (currentUser == null) {
            onResult(
                "No hay una sesión válida. Vuelve a iniciar sesión.",
            )
            return
        }

        val message = mapOf(
            "text" to normalizedText,
            "type" to type,
            "createdAt" to
                    System.currentTimeMillis(),
        )

        firestore.collection("users")
            .document(currentUser.uid)
            .collection("messages")
            .add(message)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(null)
                } else {
                    onResult(
                        "No se pudo guardar el mensaje en el historial. " +
                                "Revisa tu conexión e inténtalo nuevamente.",
                    )
                }
            }
    }

    fun loadMessages(
        onResult: (
            List<HistoryMessage>,
            String?,
        ) -> Unit,
    ) {
        val currentUser =
            auth.currentUser

        if (currentUser == null) {
            onResult(
                emptyList(),
                "No hay una sesión válida. Vuelve a iniciar sesión.",
            )
            return
        }

        firestore.collection("users")
            .document(currentUser.uid)
            .collection("messages")
            .orderBy(
                "createdAt",
                Query.Direction.DESCENDING,
            )
            .limit(50)
            .get(Source.SERVER)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val messages =
                        task.result
                            ?.documents
                            .orEmpty()
                            .map { document ->
                                HistoryMessage(
                                    id =
                                        document.id,
                                    text =
                                        document
                                            .getString(
                                                "text",
                                            )
                                            .orEmpty(),
                                    type =
                                        document
                                            .getString(
                                                "type",
                                            )
                                            ?: "Mensaje",
                                    createdAt =
                                        document
                                            .getLong(
                                                "createdAt",
                                            )
                                            ?: 0L,
                                )
                            }

                    onResult(
                        messages,
                        null,
                    )
                } else {
                    onResult(
                        emptyList(),
                        "No se pudo cargar el historial. " +
                                "Revisa tu conexión e inténtalo nuevamente.",
                    )
                }
            }
    }

    fun deleteMessage(
        messageId: String,
        onResult: (String?) -> Unit,
    ) {
        val currentUser =
            auth.currentUser

        if (currentUser == null) {
            onResult(
                "No hay una sesión válida. Vuelve a iniciar sesión.",
            )
            return
        }

        firestore.collection("users")
            .document(currentUser.uid)
            .collection("messages")
            .document(messageId)
            .delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(null)
                } else {
                    onResult(
                        "No se pudo eliminar el mensaje. " +
                                "Revisa tu conexión e inténtalo nuevamente.",
                    )
                }
            }
    }
}