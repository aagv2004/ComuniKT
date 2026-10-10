package com.example.comunikt.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.comunikt.data.MessageRepository
import com.example.comunikt.model.HistoryMessage
import com.example.comunikt.ui.UiResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    userName: String,
    profileType: String,
    communicationMode: String,
    onLogout: () -> Unit,
    onWrite: () -> Unit,
    onSpeak: () -> Unit,
    onFindDevice: () -> Unit,
    onHelp: () -> Unit,
    onQuickPhrase: (String) -> Unit,
    onSaveProfile: (
        String,
        String,
        String,
        (UiResult) -> Unit,
    ) -> Unit,
    onDeleteProfile: (
            (UiResult) -> Unit,
    ) -> Unit,
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
                    showDeleteConfirmation =
                        false
                }
            },
            title = {
                Text("¿Eliminar perfil?")
            },
            text = {
                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(
                            12.dp,
                        ),
                ) {
                    Text(
                        "Se eliminarán tu nombre y preferencias guardados. " +
                                "Tu cuenta de acceso se conservará. " +
                                "Podrás completar un nuevo perfil desde Mi perfil.",
                    )

                    deleteError?.let { message ->
                        Text(
                            text = message,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .error,
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
                            isDeleting =
                                false

                            if (
                                result.successful
                            ) {
                                showDeleteConfirmation =
                                    false

                                profileNotice =
                                    result.message
                            } else {
                                deleteError =
                                    result.message
                            }
                        }
                    },
                ) {
                    Text(
                        text =
                            if (isDeleting) {
                                "Eliminando…"
                            } else {
                                "Eliminar"
                            },
                        color =
                            MaterialTheme
                                .colorScheme
                                .error,
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation =
                            false
                    },
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
            initialName =
                userName,
            initialProfileType =
                profileType,
            initialCommunicationMode =
                communicationMode,
            onDismiss = {
                showProfileEditor =
                    false
            },
            onSave =
                onSaveProfile,
        )
    }


    HomeDashboard(
        userName = userName,
        profileType = profileType,
        communicationMode = communicationMode,
        profileNotice = profileNotice,
        historyExpanded = selectedFeature == "Historial",

        onWrite = onWrite,
        onSpeak = onSpeak,
        onFindDevice = onFindDevice,
        onHelp = onHelp,
        onLogout = onLogout,
        onQuickPhrase = onQuickPhrase,

        onProfile = {
            profileNotice = null
            showProfileEditor = true
        },

        onDeleteProfile = {
            deleteError = null
            profileNotice = null
            showDeleteConfirmation = true
        },

        onToggleHistory = {
            selectedFeature =
                if (selectedFeature == "Historial") {
                    null
                } else {
                    "Historial"
                }
        },

        historyContent = {
            HistoryTable()
        },
    )
}


@Composable
private fun HistoryTable() {
    var messages by remember {
        mutableStateOf<
                List<HistoryMessage>
                >(
            emptyList(),
        )
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var notice by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var deletingMessageId by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    fun loadHistory() {
        isLoading = true
        notice = null

        MessageRepository
            .loadMessages {
                    loadedMessages,
                    error,
                ->

                isLoading = false

                if (error == null) {
                    messages =
                        loadedMessages
                } else {
                    messages =
                        emptyList()

                    notice =
                        error
                }
            }
    }

    LaunchedEffect(Unit) {
        loadHistory()
    }

    Column(
        modifier =
            Modifier.fillMaxWidth(),
        verticalArrangement =
            Arrangement.spacedBy(
                12.dp,
            ),
    ) {
        OutlinedButton(
            onClick = {
                loadHistory()
            },
            enabled =
                !isLoading,
            modifier =
                Modifier.fillMaxWidth(),
        ) {
            Text(
                if (isLoading) {
                    "Cargando…"
                } else {
                    "Actualizar historial"
                },
            )
        }

        notice?.let { message ->
            Text(
                text =
                    message,
                style =
                    MaterialTheme
                        .typography
                        .bodyMedium,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant,
            )
        }

        when {
            isLoading -> {
                Text(
                    text =
                        "Cargando historial…",
                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,
                )
            }

            messages.isEmpty() -> {
                Text(
                    text =
                        "Todavía no hay mensajes guardados. " +
                                "Utiliza Texto a voz o Hablar para comenzar.",
                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant,
                )
            }

            else -> {
                messages.forEach {
                        message ->

                    OutlinedCard(
                        modifier =
                            Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier =
                                Modifier.padding(
                                    16.dp,
                                ),
                            verticalArrangement =
                                Arrangement.spacedBy(
                                    8.dp,
                                ),
                        ) {
                            Text(
                                text =
                                    message.text,
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyLarge,
                            )

                            Text(
                                text =
                                    "${message.type} · " +
                                            formatHistoryDate(
                                                message.createdAt,
                                            ),
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodySmall,
                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onSurfaceVariant,
                            )

                            TextButton(
                                enabled =
                                    deletingMessageId !=
                                            message.id,
                                onClick = {
                                    deletingMessageId =
                                        message.id

                                    notice = null

                                    MessageRepository
                                        .deleteMessage(
                                            message.id,
                                        ) {
                                                error ->

                                            deletingMessageId =
                                                null

                                            if (
                                                error ==
                                                null
                                            ) {
                                                messages =
                                                    messages
                                                        .filterNot {
                                                            it.id ==
                                                                    message.id
                                                        }

                                                notice =
                                                    "Mensaje eliminado del historial."
                                            } else {
                                                notice =
                                                    error
                                            }
                                        }
                                },
                            ) {
                                Text(
                                    if (
                                        deletingMessageId ==
                                        message.id
                                    ) {
                                        "Eliminando…"
                                    } else {
                                        "Eliminar"
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatHistoryDate(
    timestamp: Long,
): String {
    if (timestamp <= 0L) {
        return "Fecha no disponible"
    }

    val formatter =
        SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale(
                "es",
                "CL",
            ),
        )

    return formatter.format(
        Date(timestamp),
    )
}

@Composable
private fun ProfileEditorDialog(
    initialName: String,
    initialProfileType: String,
    initialCommunicationMode: String,
    onDismiss: () -> Unit,
    onSave: (
        String,
        String,
        String,
        (UiResult) -> Unit,
    ) -> Unit,
) {
    val colors = MaterialTheme.colorScheme

    var name by rememberSaveable {
        mutableStateOf(initialName)
    }

    var profileType by rememberSaveable {
        mutableStateOf(
            initialProfileType.takeIf {
                it == "Persona usuaria" ||
                        it == "Persona de apoyo"
            } ?: "Persona usuaria"
        )
    }

    var communicationMode by rememberSaveable {
        mutableStateOf(
            initialCommunicationMode.takeIf {
                it == "Texto" ||
                        it == "Texto y voz"
            } ?: "Texto"
        )
    }

    var isSaving by remember {
        mutableStateOf(false)
    }

    var errorMessage by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    Dialog(
        onDismissRequest = {
            if (!isSaving) onDismiss()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
        ),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.90f)
                .heightIn(max = 620.dp),
            shape = RoundedCornerShape(24.dp),
            color = colors.surface,
            border = BorderStroke(
                1.dp,
                colors.outlineVariant,
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Encabezado
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Surface(
                        modifier = Modifier.size(52.dp),
                        color = colors.secondaryContainer,
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "👤",
                                fontSize = 26.sp,
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "Mi perfil",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colors.onSurface,
                        )

                        Text(
                            text = "Personaliza tu experiencia",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurfaceVariant,
                        )
                    }
                }

                // Contenido desplazable
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "Nombre",
                            style = MaterialTheme.typography.titleSmall,
                            color = colors.onSurface,
                        )

                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                                errorMessage = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = !isSaving,
                            placeholder = {
                                Text("Tu nombre")
                            },
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            text = "Tipo de perfil",
                            style = MaterialTheme.typography.titleSmall,
                            color = colors.onSurface,
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            ProfileOptionCard(
                                label = "Persona usuaria",
                                symbol = "👤",
                                selected = profileType == "Persona usuaria",
                                enabled = !isSaving,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    profileType = "Persona usuaria"
                                },
                            )

                            ProfileOptionCard(
                                label = "Persona de apoyo",
                                symbol = "🤝",
                                selected = profileType == "Persona de apoyo",
                                enabled = !isSaving,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    profileType = "Persona de apoyo"
                                },
                            )
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            text = "Preferencia de comunicación",
                            style = MaterialTheme.typography.titleSmall,
                            color = colors.onSurface,
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            ProfileOptionCard(
                                label = "Texto",
                                symbol = "✎",
                                selected = communicationMode == "Texto",
                                enabled = !isSaving,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    communicationMode = "Texto"
                                },
                            )

                            ProfileOptionCard(
                                label = "Texto y voz",
                                symbol = "🔊",
                                selected = communicationMode == "Texto y voz",
                                enabled = !isSaving,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    communicationMode = "Texto y voz"
                                },
                            )
                        }
                    }

                    errorMessage?.let { message ->
                        Text(
                            text = message,
                            color = colors.error,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }

                // Acción principal
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSaving,
                    onClick = {
                        val normalizedName = name.trim()

                        if (
                            normalizedName.isEmpty() ||
                            normalizedName.length > 100
                        ) {
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
                    Text(
                        if (isSaving) {
                            "Guardando…"
                        } else {
                            "Guardar cambios"
                        }
                    )
                }

                TextButton(
                    onClick = onDismiss,
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}

@Composable
private fun ProfileOptionCard(
    label: String,
    symbol: String,
    selected: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme

    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.heightIn(min = 112.dp),
        shape = RoundedCornerShape(14.dp),
        color = if (selected) {
            colors.primaryContainer
        } else {
            colors.surfaceVariant
        },
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) {
                colors.primary
            } else {
                colors.outlineVariant
            },
        ),
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Text(
                text = symbol,
                fontSize = 23.sp,
            )

            Text(
                text = label,
                color = if (selected) {
                    colors.onPrimaryContainer
                } else {
                    colors.onSurfaceVariant
                },
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Medium
                },
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                text = if (selected) "✓ Seleccionado" else "Seleccionar",
                color = if (selected) {
                    colors.primary
                } else {
                    colors.onSurfaceVariant
                },
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}
