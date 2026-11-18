package com.example.appmaderera_grupo1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class ClimaPatio(
    val temperatura: Double,
    val humedadAmbiental: Int,
    val recomendacionSecado: String
)

suspend fun consultarClimaOpenMeteo(lat: Double, lon: Double): Result<ClimaPatio> =
    withContext(Dispatchers.IO) {
        try {
            val urlString =
                "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current=temperature_2m,relative_humidity_2m"
            val conexion = (URL(urlString).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 8000
                readTimeout = 8000
            }

            if (conexion.responseCode == 200) {
                val respuesta = conexion.inputStream.bufferedReader().use { it.readText() }
                val current = JSONObject(respuesta).getJSONObject("current")
                val temp = current.getDouble("temperature_2m")
                val humedad = current.getInt("relative_humidity_2m")

                val consejo = if (humedad <= 65) {
                    "Condición óptima para secado natural al aire libre."
                } else {
                    "Humedad ambiental alta: mantener lotes en cámara de secado."
                }

                Result.success(ClimaPatio(temp, humedad, consejo))
            } else {
                Result.failure(Exception("Error HTTP ${conexion.responseCode}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

@Composable
fun TarjetaClimaApi(
    latitud: Double = -33.4489,
    longitud: Double = -70.6693
) {
    val scope = rememberCoroutineScope()
    var clima by remember { mutableStateOf<ClimaPatio?>(null) }
    var cargando by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(latitud, longitud) {
        cargando = true
        consultarClimaOpenMeteo(latitud, longitud)
            .onSuccess { clima = it; errorMsg = null }
            .onFailure { errorMsg = "No se pudo conectar con la API de clima" }
        cargando = false
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Clima del Patio en Vivo (API Open-Meteo)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Coordenadas consultadas: $latitud, $longitud",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))

            when {
                cargando -> Text("Consultando estación meteorológica...")
                errorMsg != null -> Text(errorMsg!!, color = MaterialTheme.colorScheme.error)
                clima != null -> {
                    Text("Temperatura exterior: ${clima!!.temperatura} °C")
                    Text("Humedad relativa: ${clima!!.humedadAmbiental}%")
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = clima!!.recomendacionSecado,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = {
                    scope.launch {
                        cargando = true
                        consultarClimaOpenMeteo(latitud, longitud)
                            .onSuccess { clima = it; errorMsg = null }
                            .onFailure { errorMsg = "Error al actualizar desde API" }
                        cargando = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Actualizar Clima desde API")
            }
        }
    }
}