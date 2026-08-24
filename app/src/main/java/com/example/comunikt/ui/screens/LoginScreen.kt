package com.example.comunikt.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.comunikt.ui.UiResult

@Composable
fun LoginScreen(
    initialEmail: String,
    notice: String?,
    onLogin: (String, String) -> UiResult,
    onRememberEmail: (Boolean, String) -> Unit,
    onRegisterClick: () -> Unit,
    onRecoverClick: () -> Unit,
) {
    var email by rememberSaveable {
        mutableStateOf(initialEmail)
    }

    var password by rememberSaveable {
        mutableStateOf("")
    }

    var rememberEmail by rememberSaveable {
        mutableStateOf(initialEmail.isNotBlank())
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
        Text(
            text = "Iniciar Sesión",
            style = MaterialTheme.typography.headlineMedium,
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
                imeAction = ImeAction.Next,
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                resultMessage = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Contraseña")
            },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    rememberEmail = !rememberEmail
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = rememberEmail,
                onCheckedChange = {
                    rememberEmail = it
                },
            )

            Text("Recordar Correo")
        }

        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    resultSuccessful = false
                    resultMessage = "Completa el correo y la contraseña."
                } else {
                    onRememberEmail(rememberEmail, email)

                    val result = onLogin(email, password)
                    resultSuccessful = result.successful
                    resultMessage = result.message
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Ingresar")
        }

        notice?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 12.dp),
            )
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

        TextButton(
            onClick = onRecoverClick,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text("Recuperar contraseña")
        }

        TextButton(
            onClick = onRegisterClick,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text("Crear una cuenta")
        }
    }
}