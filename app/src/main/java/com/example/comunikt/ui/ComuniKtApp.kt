package com.example.comunikt.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.comunikt.model.User
import com.example.comunikt.ui.screens.LoginScreen
import com.example.comunikt.ui.screens.RecoverPassScreen
import com.example.comunikt.ui.screens.RegisterScreen
import com.example.comunikt.ui.screens.HomeScreen

enum class AuthScreen(val route: String) {
    LOGIN("login"),
    REGISTER("register"),
    RECOVER_PASSWORD("recover_password"),
    HOME("home")
}

data class UiResult(
    val successful: Boolean,
    val message: String,
)

@Composable
fun ComuniKtApp() {
    val navController = rememberNavController()


    var loginNotice by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var rememberedEmail by rememberSaveable {
        mutableStateOf("")
    }

    var loggedInUserName by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var loggedInProfileType by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var loggedInCommunicationMode by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val users = remember {
        mutableStateListOf<User>()
    }


    NavHost(
        navController = navController,
        startDestination = AuthScreen.LOGIN.route,
    ) {
        composable(AuthScreen.LOGIN.route) {
            LoginScreen(
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
                        loggedInUserName = user.name
                        loggedInProfileType = user.profileType
                        loggedInCommunicationMode = user.communicationMode
                        navController.navigate(AuthScreen.HOME.route) {
                            launchSingleTop = true
                        }

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
                    navController.navigate(AuthScreen.REGISTER.route) {
                        launchSingleTop = true
                    }
                },

                onRecoverClick = {
                    loginNotice = null
                    navController.navigate(AuthScreen.RECOVER_PASSWORD.route) {
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(AuthScreen.REGISTER.route) {
            RegisterScreen(
                registeredUserCount = users.size,

                onBack = {
                    navController.popBackStack()
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

                            navController.popBackStack()

                            UiResult(
                                successful = true,
                                message = "Cuenta creada correctamente.",
                            )
                        }
                    }
                },
            )
        }

        composable(AuthScreen.RECOVER_PASSWORD.route) {
            RecoverPassScreen(
                onBack = {
                    navController.popBackStack()
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
                            message = "Código de recuperación enviado, revise su correo.",
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

        composable(AuthScreen.HOME.route) {
            val cerrarSesion: () -> Unit = {
                loggedInUserName = null
                loggedInProfileType = null
                loggedInCommunicationMode = null

                navController.popBackStack(
                    route = AuthScreen.LOGIN.route,
                    inclusive = false,
                )

                Unit
            }

            BackHandler {
                cerrarSesion()
            }

            HomeScreen(
                userName = loggedInUserName ?: "Usuario",
                profileType = loggedInProfileType ?: "Perfil no definido",
                communicationMode = loggedInCommunicationMode ?: "Sin preferencia",
                onLogout = cerrarSesion,
            )
        }
    }
}