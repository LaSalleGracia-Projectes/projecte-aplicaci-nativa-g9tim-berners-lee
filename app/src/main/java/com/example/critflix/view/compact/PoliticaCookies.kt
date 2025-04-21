package com.example.critflix.view.compact

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.critflix.nav.Routes

@Composable
fun PoliticaCookies(navController: NavController) {
    val backgroundColor = Color.Black
    val textColor = Color.White
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // TopAppBar con título y botón de retroceso
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = textColor
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Política de Cookies",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }

        // Contenido scrolleable de la política de cookies
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .verticalScroll(scrollState)
        ) {
            // Título principal con emoji de cookie
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                Text(
                    text = "🍪 Política de Cookies y Preferencias – Critflix",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }

            Text(
                text = "Última actualización: abril de 2025",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Esta Política de Cookies explica cómo Critflix utiliza cookies y tecnologías similares para mejorar tu experiencia en la app.",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Sección 1
            SeccionPolitica(
                numero = "1",
                titulo = "¿Qué son las cookies?",
                textColor = textColor
            )

            Text(
                text = "En el contexto móvil, usamos tecnologías equivalentes a cookies (como identificadores únicos, almacenamiento local y SDKs) para almacenar información limitada sobre tu actividad en la aplicación.",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Sección 2
            SeccionPolitica(
                numero = "2",
                titulo = "Finalidad del uso",
                textColor = textColor
            )

            Text(
                text = "Usamos estas tecnologías para:",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            BulletPoint("Personalización de contenido (mostrarte películas y series acordes a tus gustos)", textColor)
            BulletPoint("Almacenamiento de preferencias de idioma, géneros favoritos y configuraciones del usuario", textColor)
            BulletPoint("Análisis interno del uso de la app para mejorar la experiencia del usuario", textColor)
            BulletPoint("Recomendaciones inteligentes basadas en tu historial de interacción", textColor)

            Spacer(modifier = Modifier.height(16.dp))

            // Sección 3
            SeccionPolitica(
                numero = "3",
                titulo = "Servicios de análisis",
                textColor = textColor
            )

            Text(
                text = "Podemos usar herramientas como Firebase Analytics u otras soluciones similares para recopilar datos anónimos y mejorar nuestras funcionalidades.",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Sección 4
            SeccionPolitica(
                numero = "4",
                titulo = "Gestión de preferencias",
                textColor = textColor
            )

            Text(
                text = "En cualquier momento puedes:",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            BulletPoint("Ajustar tus preferencias desde el menú de configuración de la app", textColor)
            BulletPoint("Desactivar recomendaciones personalizadas (lo cual podría limitar parte de la experiencia)", textColor)

            Spacer(modifier = Modifier.height(16.dp))

            // Sección 5
            SeccionPolitica(
                numero = "5",
                titulo = "Aceptación y modificaciones",
                textColor = textColor
            )

            Text(
                text = "El uso de la app implica la aceptación del uso de estas tecnologías. Esta política puede actualizarse en el futuro; cualquier cambio relevante será notificado en la app.",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}