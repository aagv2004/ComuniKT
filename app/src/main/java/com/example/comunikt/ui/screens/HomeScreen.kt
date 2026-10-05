package com.example.comunikt.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.remember
import com.example.comunikt.ui.UiResult

@Composable
fun HomeScreen(
    userName: String,
    profileType: String,
    communicationMode: String,
    onLogout: () -> Unit,
    onWrite: () -> Unit,
    onSaveProfile: (String, String, String, (UiResult) -> Unit) -> Unit,
    onDeleteProfile: ((UiResult) -> Unit) -> Unit,
) {
    var showProfileEditor by rememberSaveable {
        mutableStateOf(false)
    }

    var showDeleteConfirmation by rememberSaveable {
        mutableStateOf(false)
    }
    var isDeleting by remember {
        mutableStateOf(false)
    }
    var deleteError by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    var profileNotice by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = {
                if (!isDeleting) {
                    showDeleteConfirmation = false
                }
            },
            title = {
                Text("¿Eliminar perfil?")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        "Se eliminarán tu nombre y preferencias guardados. " +
                                "Tu cuenta de acceso se conservará. " +
                                "Podrás completar un nuevo perfil desde Mi perfil.",
                    )

                    deleteError?.let { message ->
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !isDeleting,
                    onClick = {
                        isDeleting = true
                        deleteError = null

                        onDeleteProfile { result ->
                            isDeleting = false

                            if (result.successful) {
                                showDeleteConfirmation = false
                                profileNotice = result.message
                            } else {
                                deleteError = result.message
                            }
                        }
                    },
                ) {
                    Text(
                        text = if (isDeleting) "Eliminando…" else "Eliminar",
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmation = false },
                    enabled = !isDeleting,
                ) {
                    Text("Cancelar")
                }
            },
        )
    }

    var selectedFeature by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    if (showProfileEditor) {
        ProfileEditorDialog(
            initialName = userName,
            initialProfileType = profileType,
            initialCommunicationMode = communicationMode,
            onDismiss = { showProfileEditor = false },
            onSave = onSaveProfile,
        )
    }

    val features = listOf(
        "Texto a voz",
        "Voz a texto",
        "Frases rápidas",
        "Historial"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .imePadding()
            .padding(24.dp),
    ) {
        Text(
            text = "Hola, $userName",
            style = MaterialTheme.typography.headlineMedium,
        )

        Text(
            text = "Bienvenido a ComuniKT",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 4.dp),
        )

        Text(
            text = "$profileType * Preferencia: $communicationMode",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
        )

        OutlinedButton(
            onClick = {
                profileNotice = null
                showProfileEditor = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
        ) {
            Text("Mi perfil")
        }

        TextButton(
            onClick = {
                deleteError = null
                profileNotice = null
                showDeleteConfirmation = true
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "Eliminar perfil",
                color = MaterialTheme.colorScheme.error,
            )
        }

        profileNotice?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Funciones disponibles",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "Texto a voz está disponible. Las demás funciones están en desarrollo.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(
                top = 4.dp,
                bottom = 16.dp,
            ),
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            features.chunked(2).forEach { rowFeatures ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    rowFeatures.forEach { feature ->
                        OutlinedCard(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    if (feature == "Texto a voz") {
                                        onWrite()
                                    } else {
                                        selectedFeature = feature
                                    }
                                },
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 96.dp)
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = feature,
                                    style = MaterialTheme.typography.titleSmall,
                                )
                            }
                        }
                    }
                }
            }
        }

        selectedFeature?.let { feature ->
            Spacer(modifier = Modifier.height(20.dp))

            if (feature == "Historial") {
                Text(
                    text = "Historial de ejemplo",
                    style = MaterialTheme.typography.titleMedium,
                )

                Text(
                    text = "Esta tabla representa el historial proyectado para la aplicación.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(
                        top = 4.dp,
                        bottom = 12.dp,
                    ),
                )

                HistoryTable()
            } else {
                Text(
                    text = "$feature es una función proyectada y todavía no está implementada.",
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Cerrar sesión")
        }
    }
}

@Composable
private fun HistoryTable() {
    val historyRows = listOf(
        "Registro de cuenta" to "Completado",
        "Inicio de sesión" to "Completado",
        "Texto a voz" to "Mockup",
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(8.dp),
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Acción",
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(0.65f),
            )

            Text(
                text = "Estado",
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(0.35f),
            )
        }

        historyRows.forEach { (action, status) ->
            HorizontalDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = action,
                    modifier = Modifier.weight(0.65f),
                )

                Text(
                    text = status,
                    color = if (status == "Completado") {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.weight(0.35f)
                )
            }
        }
    }
}

@Composable
private fun ProfileEditorDialog(
    initialName: String,
    initialProfileType: String,
    initialCommunicationMode: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String, (UiResult) -> Unit) -> Unit,
) {
    val profileTypes = listOf(
        "Persona usuaria",
        "Persona de apoyo",
    )
    val communicationModes = listOf(
        "Texto",
        "Texto y voz",
    )

    var name by rememberSaveable {
        mutableStateOf(initialName)
    }
    var profileType by rememberSaveable {
        mutableStateOf(
            initialProfileType.takeIf { it in profileTypes }
                ?: profileTypes.first(),
        )
    }
    var communicationMode by rememberSaveable {
        mutableStateOf(
            initialCommunicationMode.takeIf { it in communicationModes }
                ?: communicationModes.first(),
        )
    }
    var isSaving by remember {
        mutableStateOf(false)
    }
    var errorMessage by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    AlertDialog(
        onDismissRequest = {
            if (!isSaving) {
                onDismiss()
            }
        },
        title = {
            Text("Mi perfil")
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        errorMessage = null
                    },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isSaving,
                )

                Text("Tipo de perfil")

                profileTypes.forEach { option ->
                    OutlinedButton(
                        onClick = { profileType = option },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSaving,
                    ) {
                        Text(
                            if (profileType == option) {
                                "✓ $option"
                            } else {
                                option
                            },
                        )
                    }
                }

                Text("Preferencia de comunicación")

                communicationModes.forEach { option ->
                    OutlinedButton(
                        onClick = { communicationMode = option },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSaving,
                    ) {
                        Text(
                            if (communicationMode == option) {
                                "✓ $option"
                            } else {
                                option
                            },
                        )
                    }
                }

                errorMessage?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !isSaving,
                onClick = {
                    val normalizedName = name.trim()

                    if (normalizedName.isEmpty() || normalizedName.length > 100) {
                        errorMessage =
                            "El nombre debe tener entre 1 y 100 caracteres."
                    } else {
                        isSaving = true
                        errorMessage = null

                        onSave(
                            normalizedName,
                            profileType,
                            communicationMode,
                        ) { result ->
                            isSaving = false

                            if (result.successful) {
                                onDismiss()
                            } else {
                                errorMessage = result.message
                            }
                        }
                    }
                },
            ) {
                Text(if (isSaving) "Guardando…" else "Guardar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSaving,
            ) {
                Text("Cancelar")
            }
        },
    )
}