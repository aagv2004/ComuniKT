package com.example.comunikt.ui.screens

import android.util.Patterns
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
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
import com.example.comunikt.model.User
import com.example.comunikt.ui.UiResult

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onRegister: (User, (UiResult) -> Unit) -> Unit,
) {
    var isSubmitting by remember {
        mutableStateOf(false)
    }

    BackHandler(enabled = isSubmitting) {

    }

    var name by rememberSaveable {
        mutableStateOf("")
    }

    var email by rememberSaveable {
        mutableStateOf("")
    }

    var password by rememberSaveable {
        mutableStateOf("")
    }

    var confirmPassword by rememberSaveable {
        mutableStateOf("")
    }

    var acceptedTerms by rememberSaveable {
        mutableStateOf(false)
    }

    var profileType by rememberSaveable {
        mutableStateOf("Persona usuaria")
    }

    var communicationMode by rememberSaveable {
        mutableStateOf("Texto")
    }

    var menuExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    var errorMessage by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var profileTypes = listOf(
        "Persona usuaria",
        "Persona de apoyo",
    )

    var communicationModes = listOf(
        "Texto",
        "Texto y voz",
    )

    AuthLayout(
        title = "Crear cuenta",
        description = "Completa tus datos y configura tus preferencias.",
        onBack = onBack,
        backEnabled = !isSubmitting,
    ) {

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                errorMessage = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Nombre")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                errorMessage = null
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
                errorMessage = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Contraseña")
            },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next,
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                errorMessage = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Confirmar contraseña")
            },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Tipo de perfil",
            style = MaterialTheme.typography.labelLarge,
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = {
                    menuExpanded = true
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(profileType)
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = {
                    menuExpanded = false
                },
            ) {
                profileTypes.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(option)
                        },
                        onClick = {
                            profileType = option
                            menuExpanded = false
                        },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Modo de comunicación preferido",
            style = MaterialTheme.typography.labelLarge,
        )

        communicationModes.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        communicationMode = option
                    },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = communicationMode == option,
                    onClick = {
                        communicationMode = option
                    },
                )

                Text(option)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    acceptedTerms = !acceptedTerms
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = acceptedTerms,
                onCheckedChange = {
                    acceptedTerms = it
                },
            )

            Text("Acepto los términos de uso")
        }

        Button(
            onClick = {
                errorMessage = when {
                    name.isBlank() ||
                            email.isBlank() ||
                            password.isBlank() -> {
                        "Completa todos los campos obligatorios."
                    }

                    !email.esCorreoValido() -> {
                        "Ingresa un correo electrónico válido."
                    }

                    password.length < 6 -> {
                        "La contraseña debe tener al menos seis caracteres."
                    }

                    password != confirmPassword -> {
                        "Las contraseñas no coinciden."
                    }

                    !acceptedTerms -> {
                        "Debes aceptar los términos de uso."
                    }

                    else -> {
                        isSubmitting = true

                        onRegister(
                            User(
                                name = name.trim(),
                                email = email.trim(),
                                password = password,
                                profileType = profileType,
                                communicationMode = communicationMode,
                            ),
                        ) { result ->
                            isSubmitting = false

                            errorMessage = if (result.successful) {
                                null
                            } else {
                                result.message
                            }
                        }

                        null
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSubmitting,
        ) {
            Text(if (isSubmitting) "Creando cuenta..." else "Registrarse")
        }

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 12.dp),
            )
        }
    }
}

private fun String.esCorreoValido(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this.trim()).matches()
}