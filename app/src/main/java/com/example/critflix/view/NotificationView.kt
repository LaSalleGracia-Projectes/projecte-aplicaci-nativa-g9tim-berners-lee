package com.example.critflix.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.critflix.viewmodel.APIViewModel
import com.example.critflix.model.Notification

@Composable
fun NotificationView(navController: NavHostController, apiViewModel: APIViewModel) {
    // Lista de ejemplo con imágenes
    val notifications = listOf(
        Notification(
            1,
            "Ejemplo",
            "Esta es una notificación de ejemplo",
            "https://apexgamingpcs.com/cdn/shop/articles/4K-vs-1080p-for-Gaming_1600x.jpg?v=1633718118" // Reemplaza con tu URL de imagen real
        ),
        Notification(
            2,
            "Ejemplo2",
            "Esta es una notificación de ejemplo2",
            "https://wallpapers.com/images/hd/4k-gaming-background-bud9k5ffqi3r2ds9.jpg" // Reemplaza con tu URL de imagen real
        ),
        Notification(
            3,
            "Ejemplo3",
            "Esta es una notificación de ejemplo",
            "https://sobreverso.com/wp-content/uploads/2022/10/sony-playstation-controller-crash-dualshock-hd-wallpaper-preview.jpg" // Reemplaza con tu URL de imagen real
        ),
        Notification(
            4,
            "Ejemplo4",
            "Esta es una notificación de ejemplo2",
            "https://apexgamingpcs.com/cdn/shop/articles/4K-vs-1080p-for-Gaming_1600x.jpg?v=1633718118" // Reemplaza con tu URL de imagen real
        ),
        Notification(
            5,
            "Ejemplo5",
            "Esta es una notificación de ejemplo",
            "https://i.pinimg.com/736x/ef/fb/a8/effba89f77965834f4374cfb3750c530.jpg" // Reemplaza con tu URL de imagen real
        ),
        Notification(
            6,
            "Ejemplo6",
            "Esta es una notificación de ejemplo2",
            "https://apexgamingpcs.com/cdn/shop/articles/4K-vs-1080p-for-Gaming_1600x.jpg?v=1633718118" // Reemplaza con tu URL de imagen real
        ),
        Notification(
            7,
            "Ejemplo7",
            "Esta es una notificación de ejemplo",
            "https://apexgamingpcs.com/cdn/shop/articles/4K-vs-1080p-for-Gaming_1600x.jpg?v=1633718118" // Reemplaza con tu URL de imagen real
        ),
        Notification(
            8,
            "Ejemplo8",
            "Esta es una notificación de ejemplo2",
            "https://sobreverso.com/wp-content/uploads/2022/10/sony-playstation-controller-crash-dualshock-hd-wallpaper-preview.jpg" // Reemplaza con tu URL de imagen real
        ),
        Notification(
            9,
            "Ejemplo9",
            "Esta es una notificación de ejemplo",
            "https://apexgamingpcs.com/cdn/shop/articles/4K-vs-1080p-for-Gaming_1600x.jpg?v=1633718118" // Reemplaza con tu URL de imagen real
        ),
        Notification(
            10,
            "Ejemplo10",
            "Esta es una notificación de ejemplo2",
            "https://wallpapers.com/images/hd/4k-gaming-background-bud9k5ffqi3r2ds9.jpg" // Reemplaza con tu URL de imagen real
        )
        // Agrega más notificaciones según necesites
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Notificaciones",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(notifications) { notification ->
                    NotificationItem(notification, navController)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun NotificationItem(notification: Notification, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { navController },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen de la notificación usando AsyncImage
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(notification.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Notification image",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

