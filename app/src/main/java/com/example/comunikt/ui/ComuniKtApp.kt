package com.example.comunikt.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.comunikt.ui.screens.HomeScreen
import com.example.comunikt.ui.screens.HelpScreen
import com.example.comunikt.ui.screens.LoginScreen
import com.example.comunikt.ui.screens.RecoverPassScreen
import com.example.comunikt.ui.screens.RegisterScreen
import com.example.comunikt.ui.screens.SpeakScreen
import com.example.comunikt.ui.screens.WriteScreen
import com.example.comunikt.ui.screens.FindDeviceScreen
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source

enum class AuthScreen(
    val route: String,
) {
    LOGIN("login"),
    REGISTER("register"),
    RECOVER_PASSWORD("recover_password"),
    HOME("home"),
    WRITE("write"),
    SPEAK("speak"),
    FIND_DEVICE("find_device"),
    HELP("help"),
}

data class UiResult(
    val successful: Boolean,
    val message: String,
)

@Composable
fun ComuniKtApp() {
    val navController =
        rememberNavController()

    val auth = remember {
        FirebaseAuth.getInstance()
    }

    val firestore = remember {
        FirebaseFirestore.getInstance()
    }

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

    NavHost(
        navController =
            navController,
        startDestination =
            AuthScreen.LOGIN.route,
    ) {
        composable(
            AuthScreen.LOGIN.route,
        ) {
            LoginScreen(
                initialEmail =
                    rememberedEmail,
                notice =
                    loginNotice,

                onLogin = {
                        email,
                        password,
                        onResult ->

                    loginNotice = null

                    auth
                        .signInWithEmailAndPassword(
                            email.trim(),
                            password,
                        )
                        .addOnCompleteListener {
                                task ->

                            val firebaseUser =
                                if (
                                    task.isSuccessful
                                ) {
                                    task.result
                                        ?.user
                                } else {
                                    null
                                }

                            if (
                                firebaseUser !=
                                null
                            ) {
                                firestore
                                    .collection(
                                        "users",
                                    )
                                    .document(
                                        firebaseUser.uid,
                                    )
                                    .get(
                                        Source.SERVER,
                                    )
                                    .addOnCompleteListener {
                                            profileTask ->

                                        val profile =
                                            if (
                                                profileTask
                                                    .isSuccessful
                                            ) {
                                                profileTask
                                                    .result
                                            } else {
                                                null
                                            }

                                        if (
                                            profile ==
                                            null
                                        ) {
                                            auth.signOut()

                                            onResult(
                                                UiResult(
                                                    successful =
                                                        false,
                                                    message =
                                                        "Las credenciales son válidas, pero no se pudo cargar el perfil. Revisa tu conexión e inténtalo nuevamente.",
                                                ),
                                            )
                                        } else {
                                            if (
                                                profile
                                                    .exists()
                                            ) {
                                                loggedInUserName =
                                                    profile
                                                        .getString(
                                                            "name",
                                                        )
                                                        ?: firebaseUser
                                                            .email
                                                                ?: "Usuario"

                                                loggedInProfileType =
                                                    profile
                                                        .getString(
                                                            "profileType",
                                                        )

                                                loggedInCommunicationMode =
                                                    profile
                                                        .getString(
                                                            "communicationMode",
                                                        )
                                            } else {
                                                loggedInUserName =
                                                    firebaseUser
                                                        .email
                                                        ?: "Usuario"

                                                loggedInProfileType =
                                                    null

                                                loggedInCommunicationMode =
                                                    null
                                            }

                                            onResult(
                                                UiResult(
                                                    successful =
                                                        true,
                                                    message =
                                                        "Inicio de sesión correcto.",
                                                ),
                                            )

                                            navController
                                                .navigate(
                                                    AuthScreen
                                                        .HOME
                                                        .route,
                                                ) {
                                                    launchSingleTop =
                                                        true
                                                }
                                        }
                                    }
                            } else {
                                val message =
                                    when (
                                        task.exception
                                    ) {
                                        is FirebaseNetworkException ->
                                            "No se pudo conectar. Revisa tu conexión e inténtalo nuevamente."

                                        is FirebaseTooManyRequestsException ->
                                            "Se realizaron demasiados intentos. Espera y vuelve a intentarlo."

                                        else ->
                                            "No se pudo iniciar sesión. Revisa el correo y la contraseña."
                                    }

                                onResult(
                                    UiResult(
                                        successful =
                                            false,
                                        message =
                                            message,
                                    ),
                                )
                            }
                        }
                },

                onRememberEmail = {
                        shouldRemember,
                        email ->

                    rememberedEmail =
                        if (
                            shouldRemember
                        ) {
                            email.trim()
                        } else {
                            ""
                        }
                },

                onRegisterClick = {
                    loginNotice = null

                    navController
                        .navigate(
                            AuthScreen
                                .REGISTER
                                .route,
                        ) {
                            launchSingleTop =
                                true
                        }
                },

                onRecoverClick = {
                    loginNotice = null

                    navController
                        .navigate(
                            AuthScreen
                                .RECOVER_PASSWORD
                                .route,
                        ) {
                            launchSingleTop =
                                true
                        }
                },
            )
        }

        composable(
            AuthScreen.REGISTER.route,
        ) {
            RegisterScreen(
                onBack = {
                    navController
                        .popBackStack()
                },

                onRegister = {
                        newUser,
                        onResult ->

                    auth
                        .createUserWithEmailAndPassword(
                            newUser.email,
                            newUser.password,
                        )
                        .addOnCompleteListener {
                                task ->

                            if (
                                task.isSuccessful
                            ) {
                                val firebaseUser =
                                    task.result
                                        ?.user

                                if (
                                    firebaseUser ==
                                    null
                                ) {
                                    auth.signOut()

                                    onResult(
                                        UiResult(
                                            successful =
                                                false,
                                            message =
                                                "No se pudo obtener la cuenta creada.",
                                        ),
                                    )
                                } else {
                                    val profile =
                                        mapOf(
                                            "name" to
                                                    newUser
                                                        .name
                                                        .trim(),

                                            "email" to
                                                    (
                                                            firebaseUser
                                                                .email
                                                                ?: newUser
                                                                    .email
                                                                    .trim()
                                                            ),

                                            "profileType" to
                                                    newUser
                                                        .profileType,

                                            "communicationMode" to
                                                    newUser
                                                        .communicationMode,
                                        )

                                    firestore
                                        .collection(
                                            "users",
                                        )
                                        .document(
                                            firebaseUser.uid,
                                        )
                                        .set(profile)
                                        .addOnCompleteListener {
                                                profileTask ->

                                            if (
                                                profileTask
                                                    .isSuccessful
                                            ) {
                                                auth.signOut()

                                                onResult(
                                                    UiResult(
                                                        successful =
                                                            true,
                                                        message =
                                                            "Cuenta y perfil creados correctamente.",
                                                    ),
                                                )

                                                loginNotice =
                                                    "Cuenta y perfil creados correctamente. Ya puedes iniciar sesión."

                                                navController
                                                    .popBackStack(
                                                        route =
                                                            AuthScreen
                                                                .LOGIN
                                                                .route,
                                                        inclusive =
                                                            false,
                                                    )
                                            } else {
                                                firebaseUser
                                                    .delete()
                                                    .addOnCompleteListener {
                                                            rollbackTask ->

                                                        auth.signOut()

                                                        val message =
                                                            if (
                                                                rollbackTask
                                                                    .isSuccessful
                                                            ) {
                                                                "No se pudo guardar el perfil. El registro se revirtió; revisa tu conexión e inténtalo nuevamente."
                                                            } else {
                                                                "La cuenta se creó, pero no se guardó el perfil ni se pudo revertir el registro. No vuelvas a registrarla: debemos completar su perfil."
                                                            }

                                                        onResult(
                                                            UiResult(
                                                                successful =
                                                                    false,
                                                                message =
                                                                    message,
                                                            ),
                                                        )
                                                    }
                                            }
                                        }
                                }
                            } else {
                                val message =
                                    when (
                                        task.exception
                                    ) {
                                        is FirebaseAuthWeakPasswordException ->
                                            "La contraseña no cumple la política de seguridad del proyecto."

                                        is FirebaseAuthUserCollisionException ->
                                            "Ya existe una cuenta con este correo."

                                        is FirebaseAuthInvalidCredentialsException ->
                                            "El correo electrónico no es válido."

                                        is FirebaseNetworkException ->
                                            "No se pudo conectar. Revisa tu conexión e inténtalo nuevamente."

                                        is FirebaseTooManyRequestsException ->
                                            "Se realizaron demasiados intentos. Espera y vuelve a intentarlo."

                                        else ->
                                            "No se pudo crear la cuenta. Inténtalo nuevamente."
                                    }

                                onResult(
                                    UiResult(
                                        successful =
                                            false,
                                        message =
                                            message,
                                    ),
                                )
                            }
                        }
                },
            )
        }

        composable(
            AuthScreen.RECOVER_PASSWORD.route,
        ) {
            RecoverPassScreen(
                onBack = {
                    navController
                        .popBackStack()
                },

                onRecover = {
                        email,
                        onResult ->

                    auth
                        .sendPasswordResetEmail(
                            email.trim(),
                        )
                        .addOnCompleteListener {
                                task ->

                            val message =
                                if (
                                    task.isSuccessful
                                ) {
                                    "Si el correo corresponde a una cuenta, recibirás un enlace para cambiar la contraseña. Revisa también spam."
                                } else {
                                    when (
                                        task.exception
                                    ) {
                                        is FirebaseNetworkException ->
                                            "No se pudo conectar. Revisa tu conexión e inténtalo nuevamente."

                                        is FirebaseTooManyRequestsException ->
                                            "Se realizaron demasiados intentos. Espera y vuelve a intentarlo."

                                        else ->
                                            "No se pudo procesar la solicitud. Inténtalo nuevamente."
                                    }
                                }

                            onResult(
                                UiResult(
                                    successful =
                                        task.isSuccessful,
                                    message =
                                        message,
                                ),
                            )
                        }
                },
            )
        }

        composable(
            AuthScreen.WRITE.route,
        ) {
            WriteScreen(
                onBack = {
                    navController
                        .popBackStack()
                },
            )
        }

        composable(
            AuthScreen.SPEAK.route,
        ) {
            SpeakScreen(
                onBack = {
                    navController
                        .popBackStack()
                },
            )
        }

        composable(
            AuthScreen.FIND_DEVICE.route,
        ) {
            FindDeviceScreen(
                onBack = {
                    navController
                        .popBackStack()
                },
            )
        }

        composable(
            AuthScreen.HELP.route,
        ) {
            HelpScreen(
                onBack = {
                    navController.popBackStack()
                },
            )
        }

        composable(
            AuthScreen.HOME.route,
        ) {
            val cerrarSesion:
                        () -> Unit = {

                loggedInUserName =
                    null

                loggedInProfileType =
                    null

                loggedInCommunicationMode =
                    null

                auth.signOut()

                navController
                    .popBackStack(
                        route =
                            AuthScreen
                                .LOGIN
                                .route,
                        inclusive =
                            false,
                    )

                Unit
            }

            BackHandler {
                cerrarSesion()
            }

            HomeScreen(
                userName =
                    loggedInUserName
                        ?: "Usuario",

                profileType =
                    loggedInProfileType
                        ?: "Perfil no definido",

                communicationMode =
                    loggedInCommunicationMode
                        ?: "Sin preferencia",

                onLogout =
                    cerrarSesion,

                onDeleteProfile = {
                        onResult ->

                    val currentUser =
                        auth.currentUser

                    if (
                        currentUser ==
                        null
                    ) {
                        onResult(
                            UiResult(
                                successful =
                                    false,
                                message =
                                    "No hay una sesión válida. Vuelve a iniciar sesión.",
                            ),
                        )
                    } else {
                        firestore
                            .collection(
                                "users",
                            )
                            .document(
                                currentUser.uid,
                            )
                            .delete()
                            .addOnCompleteListener {
                                    task ->

                                if (
                                    task.isSuccessful
                                ) {
                                    loggedInUserName =
                                        currentUser
                                            .email
                                            ?: "Usuario"

                                    loggedInProfileType =
                                        null

                                    loggedInCommunicationMode =
                                        null
                                }

                                onResult(
                                    UiResult(
                                        successful =
                                            task.isSuccessful,
                                        message =
                                            if (
                                                task.isSuccessful
                                            ) {
                                                "Perfil eliminado. Tu cuenta de acceso se conserva."
                                            } else {
                                                "No se pudo eliminar el perfil. Revisa tu conexión e inténtalo nuevamente."
                                            },
                                    ),
                                )
                            }
                    }
                },

                onSaveProfile = {
                        name,
                        profileType,
                        communicationMode,
                        onResult ->

                    val currentUser =
                        auth.currentUser

                    val email =
                        currentUser?.email

                    if (
                        currentUser ==
                        null ||
                        email ==
                        null
                    ) {
                        onResult(
                            UiResult(
                                successful =
                                    false,
                                message =
                                    "No hay una sesión válida. Vuelve a iniciar sesión.",
                            ),
                        )
                    } else {
                        val profile =
                            mapOf(
                                "name" to
                                        name,

                                "email" to
                                        email,

                                "profileType" to
                                        profileType,

                                "communicationMode" to
                                        communicationMode,
                            )

                        firestore
                            .collection(
                                "users",
                            )
                            .document(
                                currentUser.uid,
                            )
                            .set(profile)
                            .addOnCompleteListener {
                                    task ->

                                if (
                                    task.isSuccessful
                                ) {
                                    loggedInUserName =
                                        name

                                    loggedInProfileType =
                                        profileType

                                    loggedInCommunicationMode =
                                        communicationMode
                                }

                                onResult(
                                    UiResult(
                                        successful =
                                            task.isSuccessful,

                                        message =
                                            if (
                                                task.isSuccessful
                                            ) {
                                                "Perfil guardado correctamente."
                                            } else {
                                                "No se pudo guardar el perfil. Revisa tu conexión y vuelve a intentarlo."
                                            },
                                    ),
                                )
                            }
                    }
                },

                onWrite = {
                    navController
                        .navigate(
                            AuthScreen
                                .WRITE
                                .route,
                        ) {
                            launchSingleTop =
                                true
                        }
                },

                onSpeak = {
                    navController
                        .navigate(
                            AuthScreen
                                .SPEAK
                                .route,
                        ) {
                            launchSingleTop =
                                true
                        }
                },
                onFindDevice = {
                    navController
                        .navigate(
                            AuthScreen
                                .FIND_DEVICE
                                .route,
                        ) {
                            launchSingleTop =
                                true
                        }
                },

                onHelp = {
                    navController.navigate(
                        AuthScreen.HELP.route,
                    ) {
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}