# 🪵 App Maderera V2 — Gestión de Inventario y Secado Natural

Aplicación móvil nativa desarrollada en **Kotlin (100%)** con **Jetpack Compose** bajo arquitectura **MVVM** para el control de lotes de madera, registro de evidencia en terreno y evaluación ambiental de secado (Avance Examen Final Transversal - EFT).

## 👥 Integrantes (Trabajo en Parejas)
* **[Mnauel Alvarez Guevara ]**
* **[Horacio Hidalgo2]**

---

## 🎯 Contexto y Problemática (EFT)

En los patios de acopio de madera aserrada (como *Pino Radiata* y *Eucalipto*), el secado natural al aire libre depende directamente de las variables meteorológicas locales (temperatura ambiente, humedad relativa y probabilidad de precipitaciones). Una exposición imprevista a lluvia o alta humedad deteriora la calidad de la madera, retrasa los tiempos de entrega y genera pérdidas económicas. Adicionalmente, el control manual sin respaldo fotográfico ni geolocalización dificulta la trazabilidad operativa de cada lote en patio.

**Solución propuesta:**  
**App Maderera V2** digitaliza la gestión en terreno integrando inventario, sensores nativos del dispositivo y datos meteorológicos en tiempo real para recomendar automáticamente si las condiciones ambientales permiten el secado natural al aire libre.

---

## 🚀 Funcionalidades Principales

1. **Gestión de Inventario de Madera:**
   - Visualización detallada de lotes (especie, código, volumen en $\text{m}^3$, porcentaje de humedad, estado de secado y fecha de ingreso).
2. **Integración de Recursos Nativos (Hardware):**
   - **Cámara (`CAMERA`):** Captura de evidencia fotográfica directa del lote con solicitud de permisos en tiempo de ejecución (*runtime permissions*).
   - **Geolocalización (`ACCESS_FINE_LOCATION`):** Registro de coordenadas GPS (latitud y longitud) de la ubicación del patio de acopio.
3. **Consumo de API REST Externa con Retrofit (Open-Meteo):**
   - Consulta asíncrona mediante **Retrofit 2 + Gson Converter** y corrutinas de Kotlin a la API pública **Open-Meteo** para obtener temperatura exterior, humedad relativa y probabilidad de lluvia para el día siguiente.
   - Diagnóstico automático que recomienda **secado natural al aire libre** o resguardo/cámara de secado según los umbrales climáticos.
4. **Pruebas Unitarias de Lógica de Negocio (JUnit 4):**
   - Suite de pruebas automatizadas (`ClimaSecadoTest.kt`) que valida la evaluación de condiciones de temperatura, humedad y precipitación para el secado de la madera.
5. **Compilación de Producción Firmada (Release + R8):**
   - Generación de **`app-release.apk`** firmado digitalmente mediante Keystore (`.jks`) y reglas personalizadas de optimización R8 (`rules.keep`).

---

## 📂 Estructura Principal del Código

```text
app/src/
├── main/
│   ├── java/com/example/appmaderera_grupo1/
│   │   ├── model/
│   │   │   └── LoteMadera.kt               # Modelo de datos del inventario
│   │   ├── ui/screens/
│   │   │   ├── InventarioScreen.kt         # Pantalla principal en Jetpack Compose
│   │   │   ├── RecursosNativosLote.kt      # Componente de Cámara y GPS nativos
│   │   │   └── ClimaPatioApi.kt            # Cliente Retrofit y tarjeta de API Open-Meteo
│   │   ├── ui/theme/                       # Tema, colores y tipografía Material 3
│   │   ├── viewmodel/
│   │   │   └── LoteViewModel.kt            # Gestión de estado reactivo y lógica de negocio
│   │   └── MainActivity.kt                 # Punto de entrada y gestión de permisos
│   └── keepRules/
│       └── rules.keep                      # Reglas R8/ProGuard para modo Release
└── test/
    └── java/com/example/appmaderera_grupo1/
        └── ClimaSecadoTest.kt              # Pruebas unitarias JUnit 4