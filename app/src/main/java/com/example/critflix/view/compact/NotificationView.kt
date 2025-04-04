package com.example.critflix.view.compact

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.critflix.nav.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationView(navController: NavHostController, apiViewModel: APIViewModel) {
    // Lista de ejemplo con imágenes
    val notifications = listOf(
        Notification(
            1,
            "Ejemplo",
            "Esta es una notificación de ejemplo",
            "https://apexgamingpcs.com/cdn/shop/articles/4K-vs-1080p-for-Gaming_1600x.jpg?v=1633718118"
        ),
        Notification(
            2,
            "Ejemplo2",
            "Esta es una notificación de ejemplo2",
            "https://wallpapers.com/images/hd/4k-gaming-background-bud9k5ffqi3r2ds9.jpg"
        ),
        Notification(
            3,
            "Ejemplo3",
            "Esta es una notificación de ejemplo",
            "https://sobreverso.com/wp-content/uploads/2022/10/sony-playstation-controller-crash-dualshock-hd-wallpaper-preview.jpg"
        ),
        Notification(
            4,
            "Ejemplo4",
            "Esta es una notificación de ejemplo2",
            "https://apexgamingpcs.com/cdn/shop/articles/4K-vs-1080p-for-Gaming_1600x.jpg?v=1633718118"
        ),
        Notification(
            5,
            "Ejemplo5",
            "Esta es una notificación de ejemplo",
            "https://i.pinimg.com/736x/ef/fb/a8/effba89f77965834f4374cfb3750c530.jpg"
        ),
        Notification(
            6,
            "Ejemplo6",
            "Esta es una notificación de ejemplo2",
            "https://apexgamingpcs.com/cdn/shop/articles/4K-vs-1080p-for-Gaming_1600x.jpg?v=1633718118"
        ),
        Notification(
            7,
            "Ejemplo7",
            "Esta es una notificación de ejemplo",
            "https://apexgamingpcs.com/cdn/shop/articles/4K-vs-1080p-for-Gaming_1600x.jpg?v=1633718118"
        ),
        Notification(
            8,
            "Ejemplo8",
            "Esta es una notificación de ejemplo2",
            "https://sobreverso.com/wp-content/uploads/2022/10/sony-playstation-controller-crash-dualshock-hd-wallpaper-preview.jpg"
        ),
        Notification(
            9,
            "Ejemplo9",
            "Esta es una notificación de ejemplo",
            "https://apexgamingpcs.com/cdn/shop/articles/4K-vs-1080p-for-Gaming_1600x.jpg?v=1633718118"
        ),
        Notification(
            10,
            "Ejemplo10",
            "Esta es una notificación de ejemplo2",
            "https://wallpapers.com/images/hd/4k-gaming-background-bud9k5ffqi3r2ds9.jpg"
        )
        // Agrega más notificaciones según necesites
    )

    var showOptionsBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notificaciones",
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    actionIconContentColor = Color.White,
                ),
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.Busqueda.route) }) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { showOptionsBottomSheet = true }) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Opciones",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(color = Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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

        // BottomSheet para opciones (similar al de ProfileView)
        if (showOptionsBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showOptionsBottomSheet = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    ListItem(
                        headlineContent = { Text("Marcar todas como leídas") },
                        leadingContent = { Icon(Icons.Default.Menu, contentDescription = null) },
                        modifier = Modifier.clickable {
                            showOptionsBottomSheet = false
                            /* TODO: Mark all as read action */
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Configuración de notificaciones") },
                        leadingContent = { Icon(Icons.Default.Settings, contentDescription = null) },
                        modifier = Modifier.clickable {
                            showOptionsBottomSheet = false
                            navController.navigate(Routes.Ajustes.route)
                        }
                    )
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
            .background(color = Color.Black)
            .border(width = 1.dp, color = Color.Green)
            .clickable { /* Acción al hacer clic en la notificación */ },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.Black)
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