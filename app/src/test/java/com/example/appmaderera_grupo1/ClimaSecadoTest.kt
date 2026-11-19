package com.example.appmaderera_grupo1

import com.example.appmaderera_grupo1.ui.screens.CurrentWeatherDto
import com.example.appmaderera_grupo1.ui.screens.DailyForecastDto
import com.example.appmaderera_grupo1.ui.screens.OpenMeteoResponse
import com.example.appmaderera_grupo1.ui.screens.evaluarAlertaLluvia
import com.example.appmaderera_grupo1.ui.screens.evaluarRecomendacionSecado
import com.example.appmaderera_grupo1.ui.screens.procesarRespuestaClima
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ClimaSecadoTest {

    @Test
    fun cuandoHumedadEsMenorOIgualA65_recomiendaSecadoNatural() {
        val resultado40 = evaluarRecomendacionSecado(40)
        val resultadoLimite65 = evaluarRecomendacionSecado(65)

        assertEquals("Condición óptima para secado natural al aire libre.", resultado40)
        assertEquals("Condición óptima para secado natural al aire libre.", resultadoLimite65)
    }

    @Test
    fun cuandoHumedadEsMayorA65_recomiendaCamaraDeSecado() {
        val resultado66 = evaluarRecomendacionSecado(66)
        val resultado89 = evaluarRecomendacionSecado(89)

        assertEquals("Humedad ambiental alta: mantener lotes en cámara de secado.", resultado66)
        assertEquals("Humedad ambiental alta: mantener lotes en cámara de secado.", resultado89)
    }

    @Test
    fun cuandoProbabilidadLluviaEsMayorOIgualA50_activaAlertaPreventiva() {
        val alerta50 = evaluarAlertaLluvia(50)
        val alerta80 = evaluarAlertaLluvia(80)

        assertTrue(alerta50.contains("Alerta de lluvia mañana (50%)"))
        assertTrue(alerta80.contains("Alerta de lluvia mañana (80%)"))
    }

    @Test
    fun cuandoProbabilidadLluviaEsMenorA50_indicaBajaProbabilidad() {
        val pronostico = evaluarAlertaLluvia(20)
        assertEquals("Pronóstico mañana: baja probabilidad de lluvia (20%).", pronostico)
    }

    @Test
    fun procesarRespuestaClima_mapeaCorrectamenteDtoDeRetrofit() {
        val dtoSimulado = OpenMeteoResponse(
            current = CurrentWeatherDto(temperature2m = 21.5, relativeHumidity2m = 45),
            daily = DailyForecastDto(precipitationProbabilityMax = listOf(10, 60))
        )

        val clima = procesarRespuestaClima(dtoSimulado)

        assertEquals(21.5, clima.temperatura, 0.01)
        assertEquals(45, clima.humedadAmbiental)
        assertEquals(60, clima.probLluviaManana)
        assertEquals("Condición óptima para secado natural al aire libre.", clima.recomendacionSecado)
        assertTrue(clima.alertaLluvia.contains("Alerta de lluvia mañana (60%)"))
    }
}