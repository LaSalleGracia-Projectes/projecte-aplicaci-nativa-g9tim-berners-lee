package com.example.critflix.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.Image
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.critflix.R
import com.example.critflix.Routes
import com.example.critflix.viewmodel.APIViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileView(navController: NavHostController, apiViewModel: APIViewModel) {
    var showOptionsBottomSheet by remember { mutableStateOf(false) }
    var showUserBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.Busqueda.route) }) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
                    }
                    IconButton(onClick = { showOptionsBottomSheet = true }) {
                        Icon(Icons.Default.Menu, contentDescription = "Opciones")
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
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.perfil),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { showUserBottomSheet = true }
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "usuario",
                    style = MaterialTheme.typography.titleLarge
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Seleccionar usuario"
                )
            }

            Text(
                text = "admin",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Descripción",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    )
            )

            Text(
                text = "Categorías Favoritas",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(vertical = 16.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(7) {
                    Box(
                        modifier = Modifier
                            .height(32.dp)
                            .width(80.dp)
                            .background(
                                color = Color.LightGray.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(16.dp)
                            )
                    )
                }
            }
        }

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
                        headlineContent = { Text("Editar perfil") },
                        leadingContent = { Icon(Icons.Default.Edit, contentDescription = null) },
                        modifier = Modifier.clickable {
                            showOptionsBottomSheet = false
                            /* TODO: Edit profile action */
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Configuración de la aplicación") },
                        leadingContent = { Icon(Icons.Default.Settings, contentDescription = null) },
                        modifier = Modifier.clickable {
                            showOptionsBottomSheet = false
                            /* TODO: Settings action */
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Ayuda") },
                        leadingContent = { Icon(Icons.Default.Help, contentDescription = null) },
                        modifier = Modifier.clickable {
                            showOptionsBottomSheet = false
                            /* TODO: Help action */
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Cerrar sesión") },
                        leadingContent = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
                        modifier = Modifier.clickable {
                            showOptionsBottomSheet = false
                            /* TODO: Logout action */
                        }
                    )
                }
            }
        }

        if (showUserBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showUserBottomSheet = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    ListItem(
                        headlineContent = { Text("Usuario 1") },
                        modifier = Modifier.clickable {
                            showUserBottomSheet = false
                            /* TODO: Switch to user 1 */
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Usuario 2") },
                        modifier = Modifier.clickable {
                            showUserBottomSheet = false
                            /* TODO: Switch to user 2 */
                        }
                    )
                }
            }
        }
    }
}