package com.example.appmaderera_grupo1.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.appmaderera_grupo1.model.LoteMadera

class LoteViewModel : ViewModel() {

    // Inventario inicial de prueba para la maderera
    var listaLotes by mutableStateOf(
        listOf(
            LoteMadera("1", "LOT-PINO-001", "Pino Radiata", 24.5, 12.0, "Seco / Disponible", "2026-10-15"),
            LoteMadera("2", "LOT-EUC-002", "Eucalipto", 18.0, 28.5, "En Cámara", "2026-10-20"),
            LoteMadera("3", "LOT-ROB-003", "Roble Americano", 12.0, 15.0, "Verde", "2026-11-01")
        )
    )
        private set

    var loteSeleccionado by mutableStateOf<LoteMadera?>(null)
        private set

    fun buscarPorCodigo(codigo: String) {
        loteSeleccionado = listaLotes.find { it.codigoQR == codigo }
    }
}