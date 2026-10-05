package com.example.comunikt.data

import org.junit.Assert.assertEquals
import org.junit.Test

class MessageRepositoryTest {

    @Test
    fun textoAVoz_rechazaTextoVacio() {
        comprobarRechazo(
            "",
            "No se puede guardar un mensaje vacío.",
            MessageRepository::saveTextToSpeechMessage,
        )
    }

    @Test
    fun vozATexto_rechazaTextoVacio() {
        comprobarRechazo(
            "",
            "No se puede guardar un mensaje vacío.",
            MessageRepository::saveSpeechToTextMessage,
        )
    }

    @Test
    fun textoAVoz_rechazaSoloEspacios() {
        comprobarRechazo(
            "     ",
            "No se puede guardar un mensaje vacío.",
            MessageRepository::saveTextToSpeechMessage,
        )
    }

    @Test
    fun vozATexto_rechazaTabulacionesYSaltosDeLinea() {
        comprobarRechazo(
            "\t\n\r  ",
            "No se puede guardar un mensaje vacío.",
            MessageRepository::saveSpeechToTextMessage,
        )
    }

    @Test
    fun textoAVoz_rechazaMasDe4000Caracteres() {
        comprobarRechazo(
            "a".repeat(4001),
            "El mensaje supera el límite de 4000 caracteres.",
            MessageRepository::saveTextToSpeechMessage,
        )
    }

    @Test
    fun vozATexto_rechazaMasDe4000Caracteres() {
        comprobarRechazo(
            "a".repeat(4001),
            "El mensaje supera el límite de 4000 caracteres.",
            MessageRepository::saveSpeechToTextMessage,
        )
    }

    @Test
    fun textoAVoz_rechazaEspaciosUnicode() {
        comprobarRechazo(
            "\u2003\u2003",
            "No se puede guardar un mensaje vacío.",
            MessageRepository::saveTextToSpeechMessage,
        )
    }

    private fun comprobarRechazo(
        texto: String,
        errorEsperado: String,
        guardar: (String, (String?) -> Unit) -> Unit,
    ) {
        val resultados = mutableListOf<String?>()

        guardar(texto) { error ->
            resultados.add(error)
        }

        assertEquals(
            "Debe informar exactamente un resultado.",
            1,
            resultados.size,
        )
        assertEquals(errorEsperado, resultados.single())
    }
}