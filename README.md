# 🪵 App Maderera V2 — Gestión de Inventario y Secado

Aplicación móvil nativa desarrollada en **Kotlin (100%)** con **Jetpack Compose** bajo arquitectura **MVVM** para el control de lotes de madera, registro de evidencia en terreno y evaluación ambiental de secado[cite: 4, 7, 8].

---

## 🚀 Funcionalidades Principales

1. **Gestión de Inventario de Madera:**
   - Visualización detallada de lotes (especie, código QR, volumen en $\text{m}^3$, porcentaje de humedad, estado de secado y fecha de ingreso)[cite: 4].
2. **Integración de Recursos Nativos (Hardware):**
   - **Cámara (`CAMERA`):** Captura de evidencia fotográfica directa del lote con solicitud de permisos en tiempo de ejecución (*runtime permissions*)[cite: 4].
   - **Geolocalización (`ACCESS_FINE_LOCATION`):** Registro de coordenadas GPS (latitud y longitud) de la ubicación del patio de acopio[cite: 4].
3. **Consumo de API REST Externa (Open-Meteo):**
   - Consulta asíncrona mediante corrutinas de Kotlin (`withContext(Dispatchers.IO)`) y parseo de `JSON` para obtener temperatura exterior y humedad relativa ambiental en tiempo real.
   - Diagnóstico automático que recomienda **secado natural al aire libre** (humedad $\le 65\%$) o ingreso a **cámara de secado**[cite: 7].

---

## 📂 Estructura Principal del Código

```text
app/src/main/java/com/example/appmaderera_grupo1/
├── model/
│   └── LoteMadera.kt               # Modelo de datos del inventario
└── ui/screens/
    ├── InventarioScreen.kt         # Pantalla principal en Jetpack Compose
    ├── RecursosNativosLote.kt      # Componente de Cámara y GPS nativos
    └── ClimaPatioApi.kt            # Cliente HTTP y tarjeta de API Open-Meteo
```[cite: 6]

---

## ⚙️ Entorno Técnico y Compatibilidad

Para evitar inconvenientes al abrir o compilar el proyecto en otros equipos, considerar las siguientes configuraciones aplicadas al repositorio:

- **Lenguaje:** Kotlin + Jetpack Compose (Material 3).
- **Android Gradle Plugin (AGP):** `9.3.0` (definido en `gradle/libs.versions.toml`)[cite: 1].
- **Propiedades de Gradle:** Incluye `android.r8.gradual.support=true` en `gradle.properties` para compatibilidad de compilación y optimización R8[cite: 1, 8].
- **Entorno de pruebas:** Emulador Pixel 10 (API 37.1)[cite: 1].

---

## 🛠️ Cómo ejecutar el proyecto en otro PC

1. Clonar o actualizar el repositorio:
   ```bash
   git clone [https://github.com/Horacio-hidalgo444/appmaderera.V2.git](https://github.com/Horacio-hidalgo444/appmaderera.V2.git)
