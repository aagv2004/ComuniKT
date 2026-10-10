
package com.example.comunikt.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.comunikt.R


private val Navy: Color
    @Composable get() = MaterialTheme.colorScheme.onBackground

private val Teal: Color
    @Composable get() = MaterialTheme.colorScheme.primary

private val Cloud: Color
    @Composable get() = MaterialTheme.colorScheme.background

private val Muted: Color
    @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

private val BorderColor: Color
    @Composable get() = MaterialTheme.colorScheme.outlineVariant

private val Ice: Color
    @Composable get() = MaterialTheme.colorScheme.secondaryContainer

private val White: Color
    @Composable get() = MaterialTheme.colorScheme.surface

private val NavyCard: Color
    @Composable get() = MaterialTheme.colorScheme.secondary

private val TealCard = Color(0xFF087F8C)


@Composable
fun HomeDashboard(
    userName: String,
    profileType: String,
    communicationMode: String,
    profileNotice: String?,
    historyExpanded: Boolean,
    onWrite: () -> Unit,
    onSpeak: () -> Unit,
    onFindDevice: () -> Unit,
    onHelp: () -> Unit,
    onProfile: () -> Unit,
    onDeleteProfile: () -> Unit,
    onLogout: () -> Unit,
    onToggleHistory: () -> Unit,
    onQuickPhrase: (String) -> Unit,
    historyContent: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cloud)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .imePadding()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Identidad de ComuniKT
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {

            Image(
                painter = painterResource(
                    id = R.drawable.comunikt_mark
                ),
                contentDescription = "Logo de ComuniKT",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(14.dp)),
            )

            Column {
                Text(
                    text = "ComuniKT",
                    color = Navy,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Comunicación sin barreras",
                    color = Muted,
                    fontSize = 12.sp,
                )
            }
        }

        HorizontalDivider(color = BorderColor)

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "¡Hola, $userName!",
                color = Navy,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "¿Qué necesitas hacer hoy?",
                color = Muted,
                fontSize = 15.sp,
            )
        }

        // Funciones principales
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            PrimaryAction(
                title = "Hablar",
                description = "Convierte voz en texto",
                symbol = "🎙",
                background = TealCard,
                onClick = onSpeak,
                modifier = Modifier.weight(1f),
            )

            PrimaryAction(
                title = "Texto a voz",
                description = "Reproduce tus mensajes",
                symbol = "🔊",
                background = NavyCard,
                onClick = onWrite,
                modifier = Modifier.weight(1f),
            )
        }

        // Accesos secundarios
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            SecondaryAction(
                title = "Mi perfil",
                symbol = "👤",
                onClick = onProfile,
                modifier = Modifier.weight(1f),
            )

            SecondaryAction(
                title = "Ayuda y tutorial",
                symbol = "?",
                onClick = onHelp,
                modifier = Modifier.weight(1f),
            )
        }

        // Frases rápidas
        DashboardPanel {
            Text(
                text = "Frases rápidas",
                color = Navy,
                fontSize = 19.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Text(
                text = "Selecciona una frase para comunicarla.",
                color = Muted,
                fontSize = 13.sp,
            )

            val phrases = listOf(
                "Necesito ayuda",
                "Por favor, escribe",
                "Muchas gracias",
                "¿Puedes escribirlo, por favor?",
            )

            phrases.chunked(2).forEach { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    pair.forEach { phrase ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 62.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Ice)
                                .clickable(
                                    onClickLabel = "Usar frase: $phrase",
                                    onClick = {
                                        onQuickPhrase(phrase)
                                    },
                                )
                                .padding(12.dp),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            Text(
                                text = phrase,
                                color = Navy,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 18.sp,
                            )
                        }
                    }
                }
            }
        }

        // Herramientas adicionales
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(White)
                .border(
                    1.dp,
                    BorderColor,
                    RoundedCornerShape(18.dp),
                ),
        ) {
            UtilityAction(
                symbol = "📍",
                title = "Buscar dispositivo",
                description = "Consulta tu ubicación actual",
                onClick = onFindDevice,
            )

            HorizontalDivider(color = BorderColor)

            UtilityAction(
                symbol = "◷",
                title = "Historial",
                description = "Consulta tus mensajes guardados",
                onClick = onToggleHistory,
            )
        }

        // Historial existente de Firebase
        if (historyExpanded) {
            DashboardPanel {
                Text(
                    text = "Historial de mensajes",
                    color = Navy,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.SemiBold,
                )

                historyContent()
            }
        }

        // Mensajes de actualización de perfil
        profileNotice?.let { notice ->
            DashboardPanel {
                Text(
                    text = notice,
                    color = Teal,
                    fontSize = 14.sp,
                )
            }
        }

        // Información y administración secundaria
        Text(
            text = "$profileType · Preferencia: $communicationMode",
            color = Muted,
            fontSize = 12.sp,
        )

        TextButton(
            onClick = onDeleteProfile,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "Eliminar perfil",
                color = Color(0xFFB3261E),
            )
        }

        TextButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "Cerrar sesión",
                color = Muted,
                fontWeight = FontWeight.Medium,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun PrimaryAction(
    title: String,
    description: String,
    symbol: String,
    background: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .heightIn(min = 145.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(background)
            .clickable(
                onClickLabel = "Abrir $title",
                onClick = onClick,
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = symbol,
            fontSize = 29.sp,
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = title,
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = description,
            color = Color(0xFFE7F1F9),
            fontSize = 12.sp,
            lineHeight = 16.sp,
        )
    }
}

@Composable
private fun SecondaryAction(
    title: String,
    symbol: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .heightIn(min = 98.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(White)
            .border(
                1.dp,
                BorderColor,
                RoundedCornerShape(18.dp),
            )
            .clickable(
                onClickLabel = "Abrir $title",
                onClick = onClick,
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = symbol,
            fontSize = 26.sp,
            color = Teal,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = title,
            color = Navy,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun DashboardPanel(
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(White)
            .border(
                1.dp,
                BorderColor,
                RoundedCornerShape(18.dp),
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = {
            content()
        },
    )
}

@Composable
private fun UtilityAction(
    symbol: String,
    title: String,
    description: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClickLabel = "Abrir $title",
                onClick = onClick,
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = symbol,
            fontSize = 27.sp,
            color = Teal,
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text = title,
                color = Navy,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Text(
                text = description,
                color = Muted,
                fontSize = 12.sp,
            )
        }

        Text(
            text = "›",
            color = Muted,
            fontSize = 27.sp,
        )
    }
}
