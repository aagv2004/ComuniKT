package com.example.comunikt.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.comunikt.model.User
import com.example.comunikt.ui.screens.LoginScreen
import com.example.comunikt.ui.screens.RecoverPassScreen
import com.example.comunikt.ui.screens.RegisterScreen

enum class AuthScreen {
    LOGIN,
    REGISTER,
    RECOVER_PASSWORD,
}

data class UiResult(
    val successful: Boolean,
    val message: String,
)

@Composable
fun ComuniKtApp() {
    var currentScreen by rememberSaveable {
        mutableStateOf(AuthScreen.LOGIN)
    }

    var loginNotice by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var rememberedEmail by rememberSaveable {
        mutableStateListOf<User>()
    }

    BackHandler(enabled = currentScreen != AuthScreen.LOGIN) {
        currentScreen = AuthScreen.LOGIN
    }

    when (currentScreen) {
        AuthScreen.LOGIN -> LoginScreen(
            initialEmail = rememberedEmail,
            notice = loginNotice,

            onLogin = { email, password ->
                loginNotice = null

                val user = users.firstOrNull {
                    it.email.equals(
                        other = email.trim(),
                        ignoreCase = true,
                    ) && it.password == password
                }

                if (user != null) {
                    UiResult(
                        successful = true,
                        message = "Inicio de sesión correcto. Bienvenido ${user.name}.",
                    )
                } else {
                    UiResult(
                        successful = false,
                        message = "Correo o contraseña incorrectos.",
                    )
                }
            },

            onRememberEmail = { shouldRemember, email ->
                rememberedEmail = if (shouldRemember) {
                    email.trim()
                } else {
                    ""
                }
            },

            onRegisterClick = {
                loginNotice = null
                currentScreen = AuthScreen.REGISTER
            },

            onRecoverClick = {
                loginNotice = null
                currentScreen = AuthScreen.RECOVER_PASSWORD
            },
        )

        AuthScreen.REGISTER -> RegisterScreen(
            registeredUserCount = users.size,

            onBack = {
                currentScreen = AuthScreen.LOGIN
            },

            onRegister = { newUser ->
                when {
                    users.size >= 5 -> {
                        UiResult(
                            successful = false,
                            message = "Ya se alcanzó el máximo de cinco usuarios.",
                        )
                    }

                    users.any {
                        it.email.equals(
                            other = newUser.email,
                            ignoreCase = true,
                        )
                    } -> {
                        UiResult(
                            successful = false,
                            message = "Ya existe una cuenta con este correo.",
                        )
                    }

                    else -> {
                        users.add(newUser)

                        loginNotice =
                            "Cuenta creada correctamente. Ya puedes iniciar sesión."

                        currentScreen = AuthScreen.LOGIN

                        UiResult(
                            successful = true,
                            message = "Cuenta creada correctamente.",
                        )
                    }
                }
            },
        )

        AuthScreen.RECOVER_PASSWORD -> RecoverPasswordScreen(
            onBack = {
                currentScreen = AuthScreen.LOGIN
            },

            onRecover = { email ->
                val userExists = users.any {
                    it.email.equals(
                        other = email.trim(),
                        ignoreCase = true,
                    )
                }

                if (userExists) {
                    UiResult(
                        successful = true,
                        message = "Las instrucciones simuladas fueron enviadas.",
                    )
                } else {
                    UiResult(
                        successful = false,
                        message = "No existe un usuario con ese correo.",
                    )
                }
            },
        )
    }
}