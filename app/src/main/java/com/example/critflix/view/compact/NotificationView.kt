package com.example.critflix.view.compact

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.critflix.model.Notification
import com.example.critflix.model.UserSessionManager
import com.example.critflix.nav.Routes
import com.example.critflix.viewmodel.NotificacionesViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationView(
    navController: NavHostController,
    notificacionesViewModel: NotificacionesViewModel
) {
    val context = LocalContext.current
    val sessionManager = remember { UserSessionManager(context) }
    val scope = rememberCoroutineScope()

    val userId = sessionManager.getUserId()
    val token = sessionManager.getToken() ?: ""

    val notificaciones by notificacionesViewModel.notificaciones.observeAsState(initial = emptyList())
    val isLoading by notificacionesViewModel.isLoading.observeAsState(initial = false)
    val error by notificacionesViewModel.error.observeAsState(initial = null)

    var refreshing by remember { mutableStateOf(false) }
    var showOptionsBottomSheet by remember { mutableStateOf(false) }
    var showConfirmMarkAllDialog by remember { mutableStateOf(false) }

    var showConfirmation by remember { mutableStateOf(false) }
    var confirmationMessage by remember { mutableStateOf("") }

    LaunchedEffect(userId, token) {
        if (userId > 0 && token.isNotEmpty()) {
            notificacionesViewModel.getUserNotificaciones(userId, token)
        }
    }

    fun refreshNotificaciones() {
        refreshing = true
        scope.launch {
            notificacionesViewModel.getUserNotificaciones(userId, token)
            delay(800)
            refreshing = false
        }
    }

    fun showTemporaryConfirmation(message: String) {
        confirmationMessage = message
        showConfirmation = true
        scope.launch {
            delay(2000)
            showConfirmation = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notificaciones",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF121212),
                    actionIconContentColor = Color.White,
                ),
                actions = {
                    IconButton(onClick = {
                        refreshNotificaciones()
                    }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Actualizar",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { showOptionsBottomSheet = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Opciones",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController, notificacionesViewModel)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF121212),
                            Color(0xFF000000)
                        )
                    )
                )
        ) {
            when {
                isLoading || refreshing -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = Color(0xFF1DB954),
                            strokeWidth = 4.dp
                        )
                    }
                }
                error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Error al cargar notificaciones",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error ?: "Error desconocido",
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                notificacionesViewModel.clearError()
                                refreshNotificaciones()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1DB954)
                            )
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
                notificaciones.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No tienes notificaciones",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Las notificaciones de comentarios y likes aparecerán aquí",
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        item {
                            Text(
                                text = "Tienes ${notificaciones.count { !it.leido }} notificaciones sin leer",
                                color = Color.Gray,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        itemsIndexed(notificaciones) { index, notification ->
                            NotificationItem(
                                notification = notification,
                                onClick = {
                                    notificacionesViewModel.markAsRead(notification.id, token)
                                    showTemporaryConfirmation("Notificación marcada como leída")
                                }
                            )
                            if (index < notificaciones.size - 1) {
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = showConfirmation,
                enter = fadeIn() + slideInVertically(
                    initialOffsetY = { -40 },
                    animationSpec = tween(300)
                ),
                exit = fadeOut(animationSpec = tween(300)),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1DB954)
                    ),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = confirmationMessage,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        if (showOptionsBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showOptionsBottomSheet = false },
                containerColor = Color(0xFF121212),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                ) {
                    Text(
                        text = "Opciones de notificaciones",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                    )

                    Divider(color = Color(0xFF333333))

                    ListItem(
                        headlineContent = { Text("Marcar todas como leídas", color = Color.White) },
                        supportingContent = {
                            if (notificaciones.any { !it.leido }) {
                                Text(
                                    "Tienes ${notificaciones.count { !it.leido }} notificaciones sin leer",
                                    color = Color.Gray
                                )
                            }
                        },
                        leadingContent = {
                            Icon(
                                Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF1DB954)
                            )
                        },
                        modifier = Modifier.clickable {
                            if (notificaciones.any { !it.leido }) {
                                showConfirmMarkAllDialog = true
                            } else {
                                showTemporaryConfirmation("No hay notificaciones sin leer")
                            }
                            showOptionsBottomSheet = false
                        }
                    )

                    ListItem(
                        headlineContent = { Text("Actualizar notificaciones", color = Color.White) },
                        leadingContent = {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = null,
                                tint = Color(0xFF1DB954)
                            )
                        },
                        modifier = Modifier.clickable {
                            refreshNotificaciones()
                            showOptionsBottomSheet = false
                        }
                    )

                    ListItem(
                        headlineContent = { Text("Configuración de notificaciones", color = Color.White) },
                        leadingContent = {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = null,
                                tint = Color(0xFF1DB954)
                            )
                        },
                        modifier = Modifier.clickable {
                            showOptionsBottomSheet = false
                            navController.navigate(Routes.Ajustes.route)
                        }
                    )
                }
            }
        }

        if (showConfirmMarkAllDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmMarkAllDialog = false },
                title = {
                    Text(
                        "Marcar todas como leídas",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text("¿Estás seguro de que quieres marcar todas las notificaciones como leídas?")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            notificacionesViewModel.markAllAsRead(userId, token)
                            showConfirmMarkAllDialog = false
                            showTemporaryConfirmation("Todas las notificaciones marcadas como leídas")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1DB954)
                        )
                    ) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showConfirmMarkAllDialog = false }
                    ) {
                        Text("Cancelar")
                    }
                },
                containerColor = Color(0xFF121212),
                titleContentColor = Color.White,
                textContentColor = Color.LightGray
            )
        }
    }
}

@Composable
fun NotificationItem(notification: Notification, onClick: () -> Unit) {
    val backgroundColor = if (!notification.leido) Color(0xFF1A1A1A) else Color(0xFF080808)
    val borderColor = if (!notification.leido) Color(0xFF1DB954) else Color(0xFF333333)

    val displayDate = try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM · HH:mm", Locale("es", "ES"))
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        outputFormat.timeZone = TimeZone.getDefault()

        val date = inputFormat.parse(notification.createdAt)
        date?.let { outputFormat.format(it) } ?: notification.createdAt
    } catch (e: Exception) {
        notification.createdAt
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (!notification.leido) {
            BorderStroke(1.dp, borderColor)
        } else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (!notification.leido) 4.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val (icon, iconBackgroundColor) = when (notification.tipo) {
                "nuevo_comentario" -> Pair(Icons.Default.Comment, Color(0xFF2196F3))
                "nuevo_like" -> {
                    if (notification.mensaje.contains("no le ha gustado")) {
                        Pair(Icons.Default.ThumbDown, Color(0xFFE91E63))
                    } else {
                        Pair(Icons.Default.ThumbUp, Color(0xFF1DB954))
                    }
                }
                else -> Pair(Icons.Default.Notifications, Color(0xFFFF9800))
            }

            if (!notification.leido) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1DB954))
                        .align(Alignment.Top)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconBackgroundColor.copy(alpha = 0.9f))
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = notification.mensaje,
                    color = if (!notification.leido) Color.White else Color(0xFFE0E0E0),
                    fontWeight = if (!notification.leido) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = displayDate,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}