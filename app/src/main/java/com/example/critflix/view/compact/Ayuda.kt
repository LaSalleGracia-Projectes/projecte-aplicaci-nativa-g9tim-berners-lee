package com.example.critflix.view.compact

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.critflix.nav.Routes

@Composable
fun Ayuda(navHostController: NavHostController) {
    val backgroundColor = Color.Black
    val textColor = Color.White
    val greenColor = Color(0xFF00FF0B)
    val darkGrayColor = Color.White.copy(alpha = 0.1f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                modifier = Modifier
                    .clickable { navHostController.popBackStack() }
                    .size(24.dp),
                tint = textColor
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Ayuda",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }

        // Contenido principal
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Bienvenida
            item {
                Text(
                    text = "AYUDA",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Bienvenido al centro de ayuda de nuestra app. Aquí te explicamos cómo sacarle el máximo provecho a todas nuestras funciones. Si tienes dudas, este es el lugar indicado para empezar.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Perfil de Usuario
            item {
                HelpSection(
                    icon = Icons.Default.Person,
                    title = "Perfil de Usuario",
                    backgroundColor = darkGrayColor,
                    textColor = textColor,
                    greenColor = greenColor
                ) {
                    BulletItem(text = "Registro e Inicio de Sesión: Crea tu cuenta con un correo electrónico para comenzar a usar la app.", textColor = textColor)
                    BulletItem(text = "Personalización: Sube una foto, escribe tu biografía y selecciona tus géneros favoritos.", textColor = textColor)

                    Text(
                        text = "Roles de usuario:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
                    )

                    BulletItem(text = "Usuario Básico: Accede a funciones principales como valorar, comentar y recibir recomendaciones.", textColor = textColor, indent = 32)
                    BulletItem(text = "Usuario Premium: Contenido exclusivo y acceso anticipado a información de estrenos.", textColor = textColor, indent = 32)
                    BulletItem(text = "Crítico Verificado: Comparte críticas destacadas y aparece en lo más alto de los comentarios.", textColor = textColor, indent = 32)
                }
            }

            // Valoraciones y Comentarios
            item {
                HelpSection(
                    icon = Icons.Default.Star,
                    title = "Valoraciones y Comentarios",
                    backgroundColor = darkGrayColor,
                    textColor = textColor,
                    greenColor = greenColor
                ) {
                    BulletItem(text = "Like/Dislike: Califica cualquier película o serie con \"me gusta\" o \"no me gusta\".", textColor = textColor)
                    BulletItem(text = "Comentarios: Comparte tu opinión y responde a los comentarios de otros usuarios.", textColor = textColor)
                    BulletItem(text = "Sistema de likes/dislikes en comentarios: Los comentarios más valorados suben de visibilidad.", textColor = textColor)
                }
            }

            // Críticas y Recomendaciones
            item {
                HelpSection(
                    icon = Icons.Default.RateReview,
                    title = "Críticas y Recomendaciones",
                    backgroundColor = darkGrayColor,
                    textColor = textColor,
                    greenColor = greenColor
                ) {
                    BulletItem(text = "Comentarios de usuarios: Todos pueden escribir su opinión sobre películas o series.", textColor = textColor)
                    BulletItem(text = "Críticas destacadas: Los críticos verificados aparecen destacados en los comentarios.", textColor = textColor)
                    BulletItem(text = "Sistema de recomendaciones: Basado en tus gustos e historial de interacciones.", textColor = textColor)
                }
            }

            // Listas Personalizadas
            item {
                HelpSection(
                    icon = Icons.Default.List,
                    title = "Listas Personalizadas",
                    backgroundColor = darkGrayColor,
                    textColor = textColor,
                    greenColor = greenColor
                ) {
                    BulletItem(text = "Crea listas como: \"Favoritos\", \"Por ver\", \"Viendo\" y más.", textColor = textColor)
                    BulletItem(text = "Explora listas temáticas creadas por otros usuarios (ej. \"Mejores películas de ciencia ficción\").", textColor = textColor)
                }
            }

            // Búsqueda y Filtros Avanzados
            item {
                HelpSection(
                    icon = Icons.Default.Search,
                    title = "Búsqueda y Filtros Avanzados",
                    backgroundColor = darkGrayColor,
                    textColor = textColor,
                    greenColor = greenColor
                ) {
                    BulletItem(text = "Busca títulos por nombre, género, director, año, etc.", textColor = textColor)
                    BulletItem(text = "Aplica filtros como: \"más valoradas\", \"más comentadas\", \"de estreno\" o por género.", textColor = textColor)
                }
            }

            // Recomendaciones Inteligentes
            item {
                HelpSection(
                    icon = Icons.Default.Recommend,
                    title = "Recomendaciones Inteligentes",
                    backgroundColor = darkGrayColor,
                    textColor = textColor,
                    greenColor = greenColor
                ) {
                    BulletItem(text = "Al registrarte, selecciona etiquetas de preferencia (terror, comedia, aventura, etc).", textColor = textColor)
                    BulletItem(text = "Recibe recomendaciones según tus interacciones y gustos.", textColor = textColor)
                    BulletItem(text = "Descubre títulos relacionados directamente en la página de cada película o serie.", textColor = textColor)
                }
            }

            // Página de Detalles
            item {
                HelpSection(
                    icon = Icons.Default.Info,
                    title = "Página de Detalles",
                    backgroundColor = darkGrayColor,
                    textColor = textColor,
                    greenColor = greenColor
                ) {
                    BulletItem(text = "Consulta información detallada: sinopsis, elenco, año, duración, etc.", textColor = textColor)
                    BulletItem(text = "Incluye valoraciones de críticos verificados (si están disponibles).", textColor = textColor)
                }
            }

            // Notificaciones y Recordatorios
            item {
                HelpSection(
                    icon = Icons.Default.Notifications,
                    title = "Notificaciones y Recordatorios",
                    backgroundColor = darkGrayColor,
                    textColor = textColor,
                    greenColor = greenColor
                ) {
                    BulletItem(text = "Seguimiento de títulos: Activa notificaciones para recibir actualizaciones.", textColor = textColor)
                    BulletItem(text = "Recordatorios de lanzamiento: Te avisamos cuando una película o temporada esté por estrenarse.", textColor = textColor)
                }
            }

            // Panel de Administración
            item {
                HelpSection(
                    icon = Icons.Default.AdminPanelSettings,
                    title = "Panel de Administración (para moderadores)",
                    backgroundColor = darkGrayColor,
                    textColor = textColor,
                    greenColor = greenColor
                ) {
                    BulletItem(text = "Modera comentarios, y gestiona películas o series desde un panel exclusivo.", textColor = textColor)
                    BulletItem(text = "Consulta métricas como las películas más valoradas o comentadas.", textColor = textColor)
                }
            }

            // Datos en Tiempo Real
            item {
                HelpSection(
                    icon = Icons.Default.Public,
                    title = "Datos en Tiempo Real",
                    backgroundColor = darkGrayColor,
                    textColor = textColor,
                    greenColor = greenColor
                ) {
                    BulletItem(text = "La app se actualiza automáticamente con información de bases de datos como TMDb u OMDb.", textColor = textColor)
                    BulletItem(text = "No necesitas actualizar nada manualmente.", textColor = textColor)
                }
            }

            // Spoilers
            item {
                HelpSection(
                    icon = Icons.Default.Warning,
                    title = "Spoilers",
                    backgroundColor = darkGrayColor,
                    textColor = textColor,
                    greenColor = greenColor
                ) {
                    BulletItem(text = "Los comentarios que contengan spoilers estarán marcados con una alerta para proteger tu experiencia.", textColor = textColor)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun HelpSection(
    icon: ImageVector,
    title: String,
    backgroundColor: Color,
    textColor: Color,
    greenColor: Color,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { expanded = !expanded }
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = greenColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                color = textColor
            )

            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Contraer" else "Expandir",
                tint = textColor
            )
        }

        if (expanded) {
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun BulletItem(text: String, textColor: Color, indent: Int = 16) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = indent.dp, top = 4.dp, bottom = 4.dp, end = 8.dp)
    ) {
        Text(
            text = "•",
            color = textColor,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth()
        )
    }
}