
package com.example.comunikt.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HelpScreen(
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Ayuda y tutorial de uso",
            style = MaterialTheme.typography.headlineMedium,
        )

        Text(
            text = "Aprende a utilizar las funciones de ComuniKT " +
                    "para facilitar la comunicación cotidiana.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        HelpSection(
            title = "1. Primeros pasos",
            steps = listOf(
                "Inicia sesión con tu correo y contraseña.",
                "En el menú principal encontrarás las funciones disponibles.",
                "Selecciona la herramienta que necesitas utilizar.",
                "Puedes regresar al menú principal al terminar.",
            ),
        )

        HelpSection(
            title = "2. Texto a voz",
            steps = listOf(
                "Selecciona Texto a voz en el menú principal.",
                "Escribe el mensaje que deseas comunicar.",
                "Presiona Reproducir para escuchar el mensaje.",
                "Utiliza Detener si necesitas interrumpir la reproducción.",
                "Los mensajes utilizados pueden consultarse en el historial.",
            ),
        )

        HelpSection(
            title = "3. Hablar: voz a texto",
            steps = listOf(
                "Selecciona Hablar en el menú principal.",
                "Presiona Comenzar a escuchar.",
                "Concede el permiso de micrófono si Android lo solicita.",
                "Habla claramente cerca del dispositivo.",
                "Revisa el texto reconocido en pantalla.",
                "Puedes detener la escucha, guardar el resultado " +
                        "en el historial o limpiar el contenido.",
            ),
        )

        HelpSection(
            title = "4. Buscar dispositivo",
            steps = listOf(
                "Selecciona Buscar dispositivo en el menú principal.",
                "Consulta las opciones e indicaciones disponibles " +
                        "en la pantalla de búsqueda.",
                "Regresa al menú principal cuando termines.",
            ),
        )

        HelpSection(
            title = "5. Consultar historial",
            steps = listOf(
                "Desde el menú principal, selecciona Historial.",
                "Consulta los mensajes guardados desde Texto a voz y Hablar.",
                "Presiona Actualizar historial para recargar los registros.",
                "Puedes eliminar mensajes que ya no necesites.",
            ),
        )

        HelpSection(
            title = "6. Administrar mi perfil",
            steps = listOf(
                "En el menú principal, selecciona Mi perfil.",
                "Revisa o modifica tu nombre y preferencias de comunicación.",
                "Presiona Guardar para registrar los cambios.",
                "Si necesitas recuperar tu contraseña, utiliza " +
                        "la opción disponible en la pantalla de acceso.",
            ),
        )

        Text(
            text = "Recomendaciones",
            style = MaterialTheme.typography.titleMedium,
        )

        Text(
            text = "Utiliza una conexión a internet activa. " +
                    "Comprueba los permisos solicitados por Android " +
                    "y revisa los mensajes de confirmación que muestra " +
                    "la aplicación al realizar acciones.",
            style = MaterialTheme.typography.bodyMedium,
        )

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Volver al menú principal")
        }
    }
}

@Composable
private fun HelpSection(
    title: String,
    steps: List<String>,
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )

            steps.forEachIndexed { index, step ->
                Text(
                    text = "${index + 1}. $step",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
