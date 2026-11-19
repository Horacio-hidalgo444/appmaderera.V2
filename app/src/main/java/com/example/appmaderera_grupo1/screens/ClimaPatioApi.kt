package com.example.appmaderera_grupo1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// --- 1. Modelos DTO para deserialización con Retrofit + Gson ---
data class OpenMeteoResponse(
    @SerializedName("current") val current: CurrentWeatherDto?,
    @SerializedName("daily") val daily: DailyForecastDto?
)

data class CurrentWeatherDto(
    @SerializedName("temperature_2m") val temperature2m: Double?,
    @SerializedName("relative_humidity_2m") val relativeHumidity2m: Int?
)

data class DailyForecastDto(
    @SerializedName("precipitation_probability_max") val precipitationProbabilityMax: List<Int>?
)

// --- 2. Interfaz REST de Retrofit ---
interface OpenMeteoApiService {
    @GET("v1/forecast")
    suspend fun obtenerClimaPatio(
        @Query("latitude") latitud: Double,
        @Query("longitude") longitud: Double,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m",
        @Query("daily") daily: String = "precipitation_probability_max",
        @Query("forecast_days") forecastDays: Int = 2,
        @Query("timezone") timezone: String = "auto"
    ): OpenMeteoResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://api.open-meteo.com/"

    val apiService: OpenMeteoApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenMeteoApiService::class.java)
    }
}

// --- 3. Modelo de Dominio y Lógica de Negocio (Testeable con JUnit) ---
data class ClimaPatio(
    val temperatura: Double,
    val humedadAmbiental: Int,
    val recomendacionSecado: String,
    val probLluviaManana: Int,
    val alertaLluvia: String
)

fun evaluarRecomendacionSecado(humedadAmbiental: Int): String {
    return if (humedadAmbiental <= 65) {
        "Condición óptima para secado natural al aire libre."
    } else {
        "Humedad ambiental alta: mantener lotes en cámara de secado."
    }
}

fun evaluarAlertaLluvia(probabilidadLluvia: Int): String {
    return if (probabilidadLluvia >= 50) {
        "⚠️ Alerta de lluvia mañana ($probabilidadLluvia%): cubrir pilas de secado natural."
    } else {
        "Pronóstico mañana: baja probabilidad de lluvia ($probabilidadLluvia%)."
    }
}

fun procesarRespuestaClima(respuesta: OpenMeteoResponse): ClimaPatio {
    val temp = respuesta.current?.temperature2m ?: 0.0
    val humedad = respuesta.current?.relativeHumidity2m ?: 0
    val probabilidades = respuesta.daily?.precipitationProbabilityMax
    val probManana = probabilidades?.getOrNull(1) ?: probabilidades?.getOrNull(0) ?: 0

    return ClimaPatio(
        temperatura = temp,
        humedadAmbiental = humedad,
        recomendacionSecado = evaluarRecomendacionSecado(humedad),
        probLluviaManana = probManana,
        alertaLluvia = evaluarAlertaLluvia(probManana)
    )
}

suspend fun consultarClimaOpenMeteo(lat: Double, lon: Double): Result<ClimaPatio> {
    return try {
        val respuesta = RetrofitClient.apiService.obtenerClimaPatio(
            latitud = lat,
            longitud = lon
        )
        Result.success(procesarRespuestaClima(respuesta))
    } catch (e: Exception) {
        Result.failure(e)
    }
}

// --- 4. Componente UI en Jetpack Compose ---
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
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = clima!!.alertaLluvia,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (clima!!.probLluviaManana >= 50) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSecondaryContainer
                        }
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