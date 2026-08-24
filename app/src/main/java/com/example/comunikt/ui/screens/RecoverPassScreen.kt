package com.example.comunikt.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.comunikt.ui.UiResult

@Composable
fun RecoverPassScreen(
    onBack: () -> Unit,
    onRecover: (String) -> UiResult,
) {
    var email by rememberSaveable {
        mutableStateOf("")
    }

    var resultMessage by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var resultSuccessful by rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .imePadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        TextButton(onClick = onBack) {
            Text("Volver")
        }

        Text(
            text = "Recuperar contraseña",
            style = MaterialTheme.typography.headlineMedium,
        )

        Text(
            text = "Ingresa tu correo para recibir las instrucciones.",
            modifier = Modifier.padding(top = 8.dp),
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                resultMessage = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Correo electrónico")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done,
            ),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isBlank() || !email.contains("@")) {
                    resultSuccessful = false
                    resultMessage = "Ingresa un correo electrónico válido."
                } else {
                    val result = onRecover(email)
                    resultSuccessful = result.successful
                    resultMessage = result.message
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Enviar")
        }

        resultMessage?.let {
            Text(
                text = it,
                color = if (resultSuccessful) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                },
                modifier = Modifier.padding(top = 12.dp),
            )
        }
    }
}