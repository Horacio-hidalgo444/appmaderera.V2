package com.example.appmaderera_grupo1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmaderera_grupo1.ui.screens.InventarioScreen
import com.example.appmaderera_grupo1.ui.theme.Appmaderera_grupo1Theme
import com.example.appmaderera_grupo1.viewmodel.LoteViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Appmaderera_grupo1Theme {
                val viewModel: LoteViewModel = viewModel()
                InventarioScreen(lotes = viewModel.listaLotes)
            }
        }
    }
}