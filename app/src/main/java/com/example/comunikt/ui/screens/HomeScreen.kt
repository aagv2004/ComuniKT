package com.example.comunikt.ui.screens

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    userName: String,
    profileType: String,
    communicationMode: String,
    onLogout: () -> Unit,
) {
    var selectedFeature by rememberSaveable {
        mutableStateOf<String?>(null)
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

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Funciones disponibles",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "Estas funciones forman parte del mockup de la aplicación.",
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
                                    selectedFeature = feature
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
            Text(
                text = "$feature es una función proyectada y todavía no está implementada.",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 20.dp),
            )
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