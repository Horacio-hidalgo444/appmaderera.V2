package com.example.appmaderera_grupo1.model

data class LoteMadera(
    val id: String,
    val codigoQR: String,          // Código para escanear
    val tipoMadera: String,        // Ej: Pino Radiata, Eucalipto
    val volumenM3: Double,         // Metros cúbicos
    val porcentajeHumedad: Double, // Ej: 12.5%
    val estadoSecado: String,      // "Verde", "En Cámara", "Seco / Disponible"
    val fechaIngreso: String
)