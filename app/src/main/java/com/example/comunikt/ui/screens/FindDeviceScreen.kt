package com.example.comunikt.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import java.util.Locale

@SuppressLint("MissingPermission")
@Composable
fun FindDeviceScreen(
    onBack: () -> Unit,
) {
    val context =
        LocalContext.current.applicationContext

    val locationManager =
        remember {
            context.getSystemService(
                Context.LOCATION_SERVICE,
            ) as LocationManager
        }

    var latitude by rememberSaveable {
        mutableStateOf<Double?>(null)
    }

    var longitude by rememberSaveable {
        mutableStateOf<Double?>(null)
    }

    var notice by rememberSaveable {
        mutableStateOf(
            "Pulsa el botón para obtener la ubicación actual del dispositivo.",
        )
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    fun hasFinePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasCoarsePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasLocationPermission(): Boolean {
        return hasFinePermission() ||
                hasCoarsePermission()
    }

    fun updateLocation(
        location: Location,
        message: String = "Ubicación obtenida correctamente.",
    ) {
        latitude =
            location.latitude

        longitude =
            location.longitude

        notice =
            message

        isLoading =
            false
    }

    fun requestCurrentLocation() {
        if (!hasLocationPermission()) {
            notice =
                "ComuniKT necesita permiso de ubicación para realizar esta función."

            return
        }

        val gpsEnabled =
            runCatching {
                locationManager.isProviderEnabled(
                    LocationManager.GPS_PROVIDER,
                )
            }.getOrDefault(false)

        val networkEnabled =
            runCatching {
                locationManager.isProviderEnabled(
                    LocationManager.NETWORK_PROVIDER,
                )
            }.getOrDefault(false)

        val provider =
            when {
                hasFinePermission() &&
                        gpsEnabled ->
                    LocationManager.GPS_PROVIDER

                networkEnabled ->
                    LocationManager.NETWORK_PROVIDER

                gpsEnabled ->
                    LocationManager.GPS_PROVIDER

                else ->
                    null
            }

        if (provider == null) {
            isLoading =
                false

            notice =
                "La ubicación del dispositivo está desactivada. Actívala e inténtalo nuevamente."

            return
        }

        isLoading =
            true

        notice =
            "Obteniendo ubicación…"

        val lastKnownLocation =
            runCatching {
                locationManager.getLastKnownLocation(
                    provider,
                )
            }.getOrNull()

        val locationListener =
            object : LocationListener {

                override fun onLocationChanged(
                    location: Location,
                ) {
                    updateLocation(
                        location,
                    )
                }

                override fun onProviderDisabled(
                    provider: String,
                ) {
                    if (isLoading) {
                        isLoading =
                            false

                        notice =
                            "La ubicación fue desactivada. Actívala e inténtalo nuevamente."
                    }
                }
            }

        val requestResult =
            runCatching {
                locationManager.requestSingleUpdate(
                    provider,
                    locationListener,
                    Looper.getMainLooper(),
                )
            }

        if (requestResult.isFailure) {
            if (lastKnownLocation != null) {
                updateLocation(
                    location =
                        lastKnownLocation,
                    message =
                        "Se mostró la última ubicación conocida del dispositivo.",
                )
            } else {
                isLoading =
                    false

                notice =
                    "No se pudo obtener la ubicación. Inténtalo nuevamente."
            }
        } else if (
            lastKnownLocation != null
        ) {
            updateLocation(
                location =
                    lastKnownLocation,
                message =
                    "Ubicación disponible. Actualizando si existe una posición más reciente…",
            )
        }
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts
                    .RequestMultiplePermissions(),
        ) { permissions ->

            val coarseGranted =
                permissions[
                    Manifest.permission
                        .ACCESS_COARSE_LOCATION
                ] == true

            val fineGranted =
                permissions[
                    Manifest.permission
                        .ACCESS_FINE_LOCATION
                ] == true

            if (
                coarseGranted ||
                fineGranted
            ) {
                notice =
                    "Permiso concedido. Obteniendo ubicación…"

                requestCurrentLocation()
            } else {
                notice =
                    "Se necesita permiso de ubicación para buscar este dispositivo."
            }
        }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState(),
                )
                .padding(24.dp),
        verticalArrangement =
            Arrangement.spacedBy(
                16.dp,
            ),
    ) {
        TextButton(
            onClick =
                onBack,
        ) {
            Text("Volver")
        }

        Text(
            text =
                "Buscar dispositivo",
            style =
                MaterialTheme
                    .typography
                    .headlineLarge,
        )

        Text(
            text =
                "Consulta la ubicación actual de este dispositivo.",
            style =
                MaterialTheme
                    .typography
                    .bodyLarge,
        )

        OutlinedCard(
            modifier =
                Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier =
                    Modifier.padding(
                        20.dp,
                    ),
                verticalArrangement =
                    Arrangement.spacedBy(
                        8.dp,
                    ),
            ) {
                Text(
                    text =
                        "Ubicación actual",
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                )

                if (
                    latitude != null &&
                    longitude != null
                ) {
                    Text(
                        text =
                            "Latitud: ${
                                String.format(
                                    Locale.US,
                                    "%.5f",
                                    latitude,
                                )
                            }",
                    )

                    Text(
                        text =
                            "Longitud: ${
                                String.format(
                                    Locale.US,
                                    "%.5f",
                                    longitude,
                                )
                            }",
                    )
                } else {
                    Text(
                        text =
                            "La ubicación aparecerá aquí.",
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant,
                    )
                }
            }
        }

        Button(
            enabled =
                !isLoading,
            modifier =
                Modifier.fillMaxWidth(),
            onClick = {
                if (
                    hasLocationPermission()
                ) {
                    requestCurrentLocation()
                } else {
                    notice =
                        "Solicitando permiso de ubicación…"

                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission
                                .ACCESS_COARSE_LOCATION,
                            Manifest.permission
                                .ACCESS_FINE_LOCATION,
                        ),
                    )
                }
            },
        ) {
            Text(
                if (isLoading) {
                    "Buscando…"
                } else {
                    "Obtener ubicación"
                },
            )
        }

        OutlinedButton(
            enabled =
                latitude != null &&
                        longitude != null,
            modifier =
                Modifier.fillMaxWidth(),
            onClick = {
                val lat =
                    latitude

                val lon =
                    longitude

                if (
                    lat != null &&
                    lon != null
                ) {
                    val uri =
                        Uri.parse(
                            "geo:$lat,$lon?q=$lat,$lon",
                        )

                    val intent =
                        Intent(
                            Intent.ACTION_VIEW,
                            uri,
                        ).apply {
                            addFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK,
                            )
                        }

                    runCatching {
                        context.startActivity(
                            intent,
                        )
                    }.onFailure {
                        notice =
                            "No hay una aplicación de mapas disponible para mostrar la ubicación."
                    }
                }
            },
        ) {
            Text(
                "Abrir en mapa",
            )
        }

        Text(
            text =
                notice,
            style =
                MaterialTheme
                    .typography
                    .bodyMedium,
        )
    }
}