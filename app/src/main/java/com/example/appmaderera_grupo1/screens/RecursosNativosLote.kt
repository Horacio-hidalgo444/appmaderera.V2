package com.example.appmaderera_grupo1.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.LocationManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Composable
fun SeccionRecursosNativos(
    onFotoCapturada: (Bitmap?) -> Unit = {},
    onUbicacionObtenida: (String) -> Unit = {}
) {
    val context = LocalContext.current

    // Estados locales para mostrar evidencia en pantalla
    var fotoBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var coordenadasTexto by remember { mutableStateOf("Sin ubicación registrada") }
    var mensajeEstado by remember { mutableStateOf("") }

    // --- NATIVO 1: CÁMARA ---
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            fotoBitmap = bitmap
            onFotoCapturada(bitmap)
            mensajeEstado = "Foto del lote capturada correctamente"
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            mensajeEstado = "Permiso de cámara denegado"
        }
    }

    // --- NATIVO 2: GPS / UBICACIÓN ---
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            obtenerCoordenadasNativas(context) { resultado ->
                coordenadasTexto = resultado
                onUbicacionObtenida(resultado)
            }
        } else {
            coordenadasTexto = "Permiso de ubicación denegado"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Evidencia Nativa del Lote",
                style = MaterialTheme.typography.titleMedium
            )

            // Botón Cámara
            Button(
                onClick = {
                    val permiso = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.CAMERA
                    )
                    if (permiso == PackageManager.PERMISSION_GRANTED) {
                        cameraLauncher.launch(null)
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tomar Foto del Lote (Cámara)")
            }

            // Vista previa de la foto capturada
            fotoBitmap?.let { bmp ->
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = "Foto del lote de madera",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                )
            }

            HorizontalDivider()

            // Botón GPS
            OutlinedButton(
                onClick = {
                    val fineGranted = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                    if (fineGranted) {
                        obtenerCoordenadasNativas(context) { resultado ->
                            coordenadasTexto = resultado
                            onUbicacionObtenida(resultado)
                        }
                    } else {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar Ubicación de Patio (GPS)")
            }

            Text(
                text = "GPS: $coordenadasTexto",
                style = MaterialTheme.typography.bodyMedium
            )

            if (mensajeEstado.isNotEmpty()) {
                Text(
                    text = mensajeEstado,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun obtenerCoordenadasNativas(context: Context, onResult: (String) -> Unit) {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        locationManager.getCurrentLocation(
            LocationManager.GPS_PROVIDER,
            null,
            context.mainExecutor
        ) { location ->
            if (location != null) {
                onResult("Lat: ${"%.5f".format(location.latitude)}, Lon: ${"%.5f".format(location.longitude)}")
            } else {
                val last = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (last != null) {
                    onResult("Lat: ${"%.5f".format(last.latitude)}, Lon: ${"%.5f".format(last.longitude)}")
                } else {
                    onResult("Active ubicación en el emulador (⋮ -> Location)")
                }
            }
        }
    } else {
        val last = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (last != null) {
            onResult("Lat: ${"%.5f".format(last.latitude)}, Lon: ${"%.5f".format(last.longitude)}")
        } else {
            onResult("Sin señal GPS disponible")
        }
    }
}