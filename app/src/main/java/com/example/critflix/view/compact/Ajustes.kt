package com.example.critflix.view.compact

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.critflix.nav.Routes
import com.example.critflix.viewmodel.ProfileViewModel

@Composable
fun Ajustes(navHostController: NavHostController, profileViewModel: ProfileViewModel) {
    val backgroundColor = Color.Black
    val greenColor = Color(0xFF00FF0B)
    val darkGrayColor = Color.White.copy(alpha = 0.1f)
    val textColor = Color.White
    val currentUser by profileViewModel.currentUser.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Barra superior con título y botón de retroceso
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
                text = "Configuracion",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }

        // Sección de Notificaciones
        Text(
            text = "Notificaciones",
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = textColor
        )

        AjusteItemWithSwitch(
            icon = Icons.Default.Notifications,
            title = "Permitir notificaciones",
            greenColor = greenColor,
            backgroundColor = darkGrayColor,
            textColor = textColor
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sección de Pedir Crítico
        Text(
            text = "Pedir Crítico",
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = textColor
        )

        AjusteItemWithArrow(
            icon = Icons.Default.Person,
            title = "Pedir ser crítico de Critflix",
            backgroundColor = darkGrayColor,
            textColor = textColor,
            onItemClick = {
                navHostController.navigate(Routes.SolicitudCritico.route)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sección de Cuenta
        Text(
            text = currentUser?.name ?: "Cargando...",
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = textColor
        )

        AjusteItemWithInfo(
            icon = Icons.Default.AccountCircle,
            title = currentUser?.name ?: "Cargando...",
            subtitle = currentUser?.email ?: "Cargando...",
            backgroundColor = darkGrayColor,
            textColor = textColor,
            subtitleColor = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sección de Modo oscuro
        Text(
            text = "Modo oscuro",
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = textColor
        )

        AjusteItemWithSwitch(
            icon = Icons.Default.DarkMode,
            title = "Modo oscuro",
            greenColor = greenColor,
            backgroundColor = darkGrayColor,
            textColor = textColor,
            initialChecked = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sección de Términos legales
        Text(
            text = "Términos legales",
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = textColor
        )

        AjusteItemWithIcon(
            icon = Icons.Default.PrivacyTip,
            title = "Privacidad",
            backgroundColor = darkGrayColor,
            textColor = textColor,
            onItemClick = { navHostController.navigate(Routes.PoliticaPrivacidad.route) }
        )

        AjusteItemWithIcon(
            icon = Icons.Default.Security,
            title = "Seguridad",
            backgroundColor = darkGrayColor,
            textColor = textColor,
            onItemClick = { navHostController.navigate(Routes.PoliticaSeguridad.route) }
        )

        AjusteItemWithIcon(
            icon = Icons.Default.Cookie,
            title = "Preferencias de cookies",
            backgroundColor = darkGrayColor,
            textColor = textColor,
            onItemClick = { navHostController.navigate(Routes.PoliticaCookies.route) }
        )

        AjusteItemWithIcon(
            icon = Icons.Default.Description,
            title = "Términos de uso",
            backgroundColor = darkGrayColor,
            textColor = textColor,
            onItemClick = { navHostController.navigate("terminos") }
        )
    }
}

@Composable
fun AjusteItemWithSwitch(
    icon: ImageVector,
    title: String,
    greenColor: Color = Color(0xFF00FF0A),
    backgroundColor: Color = Color.LightGray.copy(alpha = 0.2f),
    textColor: Color = Color.Black,
    initialChecked: Boolean = false
) {
    var isChecked by remember { mutableStateOf(initialChecked) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = if (isChecked) greenColor else textColor
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            color = textColor
        )
        Switch(
            checked = isChecked,
            onCheckedChange = { isChecked = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = greenColor,
                checkedTrackColor = greenColor.copy(alpha = 0.5f),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.Gray.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
fun AjusteItemWithArrow(
    icon: ImageVector,
    title: String,
    backgroundColor: Color = Color.LightGray.copy(alpha = 0.2f),
    textColor: Color = Color.Black,
    onItemClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onItemClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = textColor
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            color = textColor
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Más opciones",
            modifier = Modifier.size(24.dp),
            tint = textColor
        )
    }
}

@Composable
fun AjusteItemWithInfo(
    icon: ImageVector,
    title: String,
    subtitle: String,
    backgroundColor: Color = Color.LightGray.copy(alpha = 0.2f),
    textColor: Color = Color.Black,
    subtitleColor: Color = Color.Gray
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = textColor
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = subtitleColor
            )
        }
    }
}

@Composable
fun AjusteItemWithIcon(
    icon: ImageVector,
    title: String,
    backgroundColor: Color = Color.LightGray.copy(alpha = 0.2f),
    textColor: Color = Color.Black,
    onItemClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onItemClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = textColor
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            color = textColor
        )
    }
}