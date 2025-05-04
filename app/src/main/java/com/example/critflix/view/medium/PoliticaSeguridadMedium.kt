package com.example.critflix.view.medium

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
fun PoliticaSeguridadMedium(navController: NavController) {
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
                text = "Política de Seguridad",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }

        // Contenido scrolleable de la política de seguridad
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .verticalScroll(scrollState)
        ) {
            // Título principal con ícono de seguridad
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                Text(
                    text = "🔒 Política de Seguridad – Critflix",
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
                text = "En Critflix, la seguridad de la información de nuestros usuarios es fundamental. Esta política describe las medidas técnicas y organizativas que aplicamos para proteger los datos personales y asegurar el uso confiable de la plataforma.",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Sección 1
            SeccionPolitica(
                numero = "1",
                titulo = "Autenticación segura",
                textColor = textColor
            )

            BulletPoint("El acceso a la app requiere registro mediante correo electrónico.", textColor)
            BulletPoint("La autenticación se realiza a través de sistemas seguros y con validación por correo electrónico.", textColor)
            BulletPoint("Las contraseñas se almacenan en forma cifrada, utilizando algoritmos de hash estándar del sector.", textColor)

            Spacer(modifier = Modifier.height(16.dp))

            // Sección 2
            SeccionPolitica(
                numero = "2",
                titulo = "Protección de la información",
                textColor = textColor
            )

            BulletPoint("Toda la información transmitida entre el usuario y nuestros servidores está protegida mediante cifrado TLS (HTTPS).", textColor)
            BulletPoint("Implementamos medidas para prevenir accesos no autorizados, ataques de fuerza bruta y exposición de datos.", textColor)

            Spacer(modifier = Modifier.height(16.dp))

            // Sección 3
            SeccionPolitica(
                numero = "3",
                titulo = "Control de acceso y roles",
                textColor = textColor
            )

            BulletPoint("Critflix implementa un sistema de roles (Usuario Básico, Usuario Premium, Crítico Verificado) con permisos diferenciados para el acceso a funciones específicas.", textColor)
            BulletPoint("El panel de administración es de acceso restringido y está protegido mediante autenticación avanzada.", textColor)

            Spacer(modifier = Modifier.height(16.dp))

            // Sección 4
            SeccionPolitica(
                numero = "4",
                titulo = "Detección de vulnerabilidades y actualizaciones",
                textColor = textColor
            )

            BulletPoint("Realizamos revisiones periódicas del sistema y actualizaciones de seguridad.", textColor)
            BulletPoint("Cualquier vulnerabilidad identificada es tratada de forma prioritaria.", textColor)
            BulletPoint("Las dependencias externas y servicios de terceros se actualizan regularmente para evitar riesgos de seguridad.", textColor)

            Spacer(modifier = Modifier.height(16.dp))

            // Sección 5
            SeccionPolitica(
                numero = "5",
                titulo = "Responsabilidad del usuario",
                textColor = textColor
            )

            Text(
                text = "Los usuarios deben mantener sus credenciales en privado y utilizar contraseñas seguras. Critflix no se hace responsable por accesos no autorizados derivados de negligencia del usuario.",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}