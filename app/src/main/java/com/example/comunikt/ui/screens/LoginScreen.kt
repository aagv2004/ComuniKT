package com.example.comunikt.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    onLogin: (String, String, (UiResult) -> Unit) -> Unit,
    onRememberEmail: (Boolean, String) -> Unit,
    onRegisterClick: () -> Unit,
    onRecoverClick: () -> Unit,
) {
    var isSubmitting by remember {
        mutableStateOf(false)
    }

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

    BackHandler(enabled = isSubmitting) {
        // Esperamos la respuesta antes de salir.
    }

    AuthLayout(
        title = "Iniciar sesión",
        description = "Ingresa a tu cuenta para comenzar a comunicarte.",
        centerContent = true,
    ) {

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                resultMessage = null

                if (rememberEmail) {
                    onRememberEmail(true, email)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSubmitting,
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
            enabled = !isSubmitting,
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
                .clickable(enabled = !isSubmitting) {
                    rememberEmail = !rememberEmail
                    onRememberEmail(rememberEmail, email)
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = rememberEmail,
                onCheckedChange = { checked ->
                    rememberEmail = checked
                    onRememberEmail(checked, email)
                },
                enabled = !isSubmitting,
            )

            Text("Recordar Correo")
        }

        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    resultSuccessful = false
                    resultMessage = "Completa el correo y la contraseña."
                } else {
                    isSubmitting = true
                    resultSuccessful = false
                    resultMessage = null

                    onRememberEmail(rememberEmail, email.trim())

                    onLogin(email.trim(), password) { result ->
                        isSubmitting = false
                        resultSuccessful = result.successful
                        resultMessage = if (result.successful) null else result.message

                        if (result.successful) {
                            password = ""
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSubmitting,
        ) {
            Text(
                if (isSubmitting) {
                    "Ingresando…"
                } else {
                    "Ingresar"
                },
            )
        }

        notice?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 12.dp),
            )
        }

        resultMessage?.let { message ->
            Text(
                text = message,
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
            enabled = !isSubmitting,
        ) {
            Text("Recuperar contraseña")
        }

        TextButton(
            onClick = onRegisterClick,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            enabled = !isSubmitting,
        ) {
            Text("Crear una cuenta")
        }
    }
}