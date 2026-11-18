package com.example.appmaderera_grupo1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appmaderera_grupo1.model.LoteMadera

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventarioScreen(lotes: List<LoteMadera>) {
    // Estados dinámicos que conectan el GPS con la API de clima
    var latitudPatio by remember { mutableStateOf(-33.4489) }
    var longitudPatio by remember { mutableStateOf(-70.6693) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventario de Maderera 🪵") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Tarjeta de Recursos Nativos (Cámara + GPS)
            item {
                SeccionRecursosNativos(
                    onFotoCapturada = { bitmap ->
                        // Evidencia fotográfica capturada
                    },
                    onUbicacionObtenida = { coords ->
                        val valores = Regex("-?\\d+\\.\\d+")
                            .findAll(coords.toString())
                            .map { it.value.toDouble() }
                            .toList()
                        if (valores.size >= 2) {
                            latitudPatio = valores[0]
                            longitudPatio = valores[1]
                        }
                    }
                )
            }

            // 2. Tarjeta de Consumo de API Externa conectada al GPS
            item {
                TarjetaClimaApi(
                    latitud = latitudPatio,
                    longitud = longitudPatio
                )
            }

            // 3. Listado de lotes de madera
            items(lotes) { lote ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = lote.tipoMadera,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Código: ${lote.codigoQR}")
                        Text(text = "Volumen: ${lote.volumenM3} m³")
                        Text(text = "Humedad: ${lote.porcentajeHumedad}%")
                        Text(text = "Estado: ${lote.estadoSecado}")
                        Text(text = "Ingreso: ${lote.fechaIngreso}")
                    }
                }
            }
        }
    }
}