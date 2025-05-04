package com.example.critflix.view.compact

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.critflix.R
import com.example.critflix.model.UserSessionManager
import com.example.critflix.nav.Routes
import com.example.critflix.viewmodel.APIViewModel
import com.example.critflix.viewmodel.NotificacionesViewModel
import com.example.critflix.viewmodel.ProfileViewModel
import com.example.critflix.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileView(navController: NavHostController, apiViewModel: APIViewModel, profileViewModel: ProfileViewModel, userViewModel: UserViewModel, notificacionesViewModel: NotificacionesViewModel) {
    var showOptionsBottomSheet by remember { mutableStateOf(false) }
    var showUserBottomSheet by remember { mutableStateOf(false) }
    val profileState by profileViewModel.profileState.observeAsState()
    val currentUser by profileViewModel.currentUser.observeAsState()
    val context = LocalContext.current
    val sessionManager = remember { UserSessionManager(context) }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    actionIconContentColor = Color.White,
                ),
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
            BottomNavigationBar(navController, notificacionesViewModel)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(color = Color.Black)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen de perfil del usuario
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .padding(top = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.user),
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
                    text = currentUser?.name ?: "Cargando...",
                    style = MaterialTheme.typography.titleLarge
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Seleccionar usuario"
                )
            }

            Text(
                text = currentUser?.rol ?: "usuario",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Descripción
            Text(
                text = "Descripción",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                ),
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = currentUser?.biografia ?: "No hay descripción disponible",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Categorías Favoritas",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                ),
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(vertical = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(7) { index ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .height(36.dp)
                            .width(100.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Categoría ${index + 1}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
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
                            navController.navigate(Routes.EditarPerfil.route)
                            showOptionsBottomSheet = false
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Configuración de la aplicación") },
                        leadingContent = { Icon(Icons.Default.Settings, contentDescription = null) },
                        modifier = Modifier.clickable {
                            navController.navigate(Routes.Ajustes.route)
                            showOptionsBottomSheet = false
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Ayuda") },
                        leadingContent = { Icon(Icons.Default.Help, contentDescription = null) },
                        modifier = Modifier.clickable {
                            navController.navigate(Routes.Ayuda.route)
                            showOptionsBottomSheet = false
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Cerrar sesión") },
                        leadingContent = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
                        modifier = Modifier.clickable {
                            showOptionsBottomSheet = false
                            sessionManager.logout(navController)
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
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Usuario 2") },
                        modifier = Modifier.clickable {
                            showUserBottomSheet = false
                        }
                    )
                }
            }
        }
    }
}