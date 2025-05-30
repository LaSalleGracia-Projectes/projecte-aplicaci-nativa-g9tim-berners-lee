package com.example.critflix.view.expanded

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
import com.example.critflix.R
import com.example.critflix.model.UserSessionManager
import com.example.critflix.nav.Routes
import com.example.critflix.viewmodel.APIViewModel
import com.example.critflix.viewmodel.NotificacionesViewModel
import com.example.critflix.viewmodel.ProfileViewModel
import com.example.critflix.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileViewExpanded(navController: NavHostController, apiViewModel: APIViewModel, profileViewModel: ProfileViewModel, userViewModel: UserViewModel, notificacionesViewModel: NotificacionesViewModel, deviceType: String) {
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
                title = { Text("Mi Perfil", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    actionIconContentColor = Color.White,
                ),
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.BusquedaExpanded.route) }) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
                    }
                    IconButton(onClick = { showOptionsBottomSheet = true }) {
                        Icon(Icons.Default.Menu, contentDescription = "Opciones")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                navController,
                notificacionesViewModel
            )
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
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Seleccionar usuario",
                    tint = Color.White
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
                    color = Color.White
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
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = currentUser?.biografia ?: "No hay descripción disponible",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Categorías Favoritas",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White
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
                onDismissRequest = { showOptionsBottomSheet = false },
                containerColor = Color.Black,
                scrimColor = Color.Black.copy(alpha = 0.5f),
                tonalElevation = 0.dp,
                contentColor = Color.White,
                dragHandle = { Box(modifier = Modifier.height(4.dp).width(40.dp).background(Color.Gray, RoundedCornerShape(2.dp))) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.Black)
                        .padding(16.dp)
                ) {
                    ListItem(
                        headlineContent = { Text("Editar perfil", color = Color.White) },
                        leadingContent = { Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White) },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Black,
                            headlineColor = Color.White,
                            leadingIconColor = Color.White
                        ),
                        modifier = Modifier
                            .clickable {
                                navController.navigate(Routes.EditarPerfilExpanded.route)
                                showOptionsBottomSheet = false
                            }
                    )
                    ListItem(
                        headlineContent = { Text("Configuración de la aplicación", color = Color.White) },
                        leadingContent = { Icon(Icons.Default.Settings, contentDescription = null, tint = Color.White) },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Black,
                            headlineColor = Color.White,
                            leadingIconColor = Color.White
                        ),
                        modifier = Modifier.clickable {
                            navController.navigate(Routes.AjustesExpanded.route)
                            showOptionsBottomSheet = false
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Ayuda", color = Color.White) },
                        leadingContent = { Icon(Icons.Default.Help, contentDescription = null, tint = Color.White) },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Black,
                            headlineColor = Color.White,
                            leadingIconColor = Color.White
                        ),
                        modifier = Modifier.clickable {
                            navController.navigate(Routes.AyudaExpanded.route)
                            showOptionsBottomSheet = false
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Cerrar sesión", color = Color.White) },
                        leadingContent = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.White) },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Black,
                            headlineColor = Color.White,
                            leadingIconColor = Color.White
                        ),
                        modifier = Modifier.clickable {
                            showOptionsBottomSheet = false
                            sessionManager.logout(navController, deviceType)
                        }
                    )
                }
            }
        }

        /*if (showUserBottomSheet) {
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
        }*/
    }
}