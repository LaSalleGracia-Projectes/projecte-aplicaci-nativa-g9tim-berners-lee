package com.example.critflix.view.compact

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun PoliticaPrivacidad(navController: NavController) {
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
                text = "Política de Privacidad",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }

        // Contenido scrolleable de la política de privacidad
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .verticalScroll(scrollState)
        ) {
            // Título principal
            Text(
                text = "Política de Privacidad – Critflix",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            Text(
                text = "Última actualización: abril de 2025",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "En Critflix, la privacidad de nuestros usuarios es una prioridad. Esta Política de Privacidad explica cómo recopilamos, usamos, almacenamos y protegemos tu información personal cuando utilizas nuestra aplicación móvil y otros servicios relacionados.",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Sección 1
            SeccionPolitica(
                numero = "1",
                titulo = "Información que recopilamos",
                textColor = textColor
            )

            Text(
                text = "Durante el uso de Critflix, podemos recopilar los siguientes datos personales:",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            BulletPoint("Nombre", textColor)
            BulletPoint("Dirección de correo electrónico", textColor)
            BulletPoint("Preferencias de contenido (géneros favoritos, etiquetas, listas personalizadas)", textColor)
            BulletPoint("Imagen de perfil (si decides subirla)", textColor)
            BulletPoint("Historial de interacciones (valoraciones, comentarios, favoritos, etc.)", textColor)

            Spacer(modifier = Modifier.height(16.dp))

            // Sección 2
            SeccionPolitica(
                numero = "2",
                titulo = "Finalidad del tratamiento de datos",
                textColor = textColor
            )

            Text(
                text = "La información recopilada se utiliza para:",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            BulletPoint("Gestionar tu cuenta y autenticación de inicio de sesión", textColor)
            BulletPoint("Personalizar tu experiencia y recomendaciones", textColor)
            BulletPoint("Mostrar críticas y valoraciones relevantes", textColor)
            BulletPoint("Permitir la interacción con otros usuarios mediante comentarios, debates y reacciones", textColor)
            BulletPoint("Proporcionar acceso a funciones avanzadas según tu rol (usuario, premium, crítico)", textColor)
            BulletPoint("Enviarte notificaciones de estrenos, actualizaciones o recordatorios si lo habilitas", textColor)

            Spacer(modifier = Modifier.height(16.dp))

            // Sección 3
            SeccionPolitica(
                numero = "3",
                titulo = "Servicios de terceros",
                textColor = textColor
            )

            Text(
                text = "Critflix utiliza los siguientes servicios de terceros que pueden acceder a datos limitados:",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            BulletPoint("Digital Ocean (alojamiento y procesamiento de datos)", textColor)
            BulletPoint("The Movie Database (TMDb) (información sobre películas y series)", textColor)
            BulletPoint("Servicios de autenticación basados en correo electrónico y encriptación de contraseñas", textColor)

            Spacer(modifier = Modifier.height(16.dp))

            // Sección 4
            SeccionPolitica(
                numero = "4",
                titulo = "Conservación y seguridad de los datos",
                textColor = textColor
            )

            Text(
                text = "Los datos personales se almacenan de manera segura en servidores protegidos. Utilizamos protocolos de cifrado y control de acceso para prevenir el acceso no autorizado. Las contraseñas están encriptadas y no se almacenan en texto plano.",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Sección 5
            SeccionPolitica(
                numero = "5",
                titulo = "Derechos del usuario",
                textColor = textColor
            )

            Text(
                text = "Tienes derecho a:",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            BulletPoint("Acceder, modificar o eliminar tu información personal", textColor)
            BulletPoint("Solicitar la cancelación de tu cuenta en cualquier momento", textColor)
            BulletPoint("Solicitar información sobre el tratamiento de tus datos", textColor)

            Text(
                text = "Para ejercer tus derechos, puedes contactarnos a través del correo: privacidad@critflix.com",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Sección 6
            SeccionPolitica(
                numero = "6",
                titulo = "Cambios en esta política",
                textColor = textColor
            )

            Text(
                text = "Nos reservamos el derecho de actualizar esta política. Se notificará a los usuarios sobre cambios relevantes dentro de la app o mediante correo electrónico si fuera necesario.",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}

@Composable
fun SeccionPolitica(numero: String, titulo: String, textColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Text(
            text = "$numero. $titulo",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun BulletPoint(text: String, textColor: Color) {
    Row(
        modifier = Modifier.padding(bottom = 4.dp, start = 8.dp)
    ) {
        Text(
            text = "• ",
            color = textColor,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}