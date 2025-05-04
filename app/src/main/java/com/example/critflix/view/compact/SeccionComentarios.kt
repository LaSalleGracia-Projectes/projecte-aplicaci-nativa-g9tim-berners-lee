package com.example.critflix.view.compact

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.critflix.model.Comentario
import com.example.critflix.model.UserSessionManager
import com.example.critflix.viewmodel.ComentariosViewModel
import com.example.critflix.viewmodel.RespuestasViewModel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.take
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SeccionComentarios(
    tmdbId: Int,
    tipo: String,
    comentariosViewModel: ComentariosViewModel,
    respuestasViewModel: RespuestasViewModel
) {
    val context = LocalContext.current
    val userSessionManager = remember { UserSessionManager(context) }
    val userId = userSessionManager.getUserId()

    var comentarioText by remember { mutableStateOf("") }
    var esSpoiler by remember { mutableStateOf(false) }

    // Estado para manejar el diálogo de respuestas
    var showRespuestasDialog by remember { mutableStateOf(false) }
    var selectedComentario by remember { mutableStateOf<Comentario?>(null) }

    val comentarios by comentariosViewModel.comentarios.observeAsState(emptyList())
    val isLoading by comentariosViewModel.isLoading.observeAsState(false)
    val error by comentariosViewModel.error.observeAsState()
    val comentarioCreado by comentariosViewModel.comentarioCreado.observeAsState()

    val token = userSessionManager.getToken() ?: ""
    val commentsCacheKey = remember(tmdbId, tipo) { "$tmdbId-$tipo" }

    LaunchedEffect(commentsCacheKey) {
        comentariosViewModel.clearComentarios()
        comentariosViewModel.getComentariosByTmdbId(tmdbId, tipo, token)
    }

    LaunchedEffect(commentsCacheKey) {
        if (userId > 0) {
            snapshotFlow { comentarios }
                .filter { it.isNotEmpty() }
                .take(1)
                .collect { listaComentarios ->
                    if (listaComentarios.isNotEmpty() &&
                        listaComentarios.first().tmdbId == tmdbId &&
                        listaComentarios.first().tipo == tipo) {

                        val comentariosConDefaultStatus = listaComentarios.map {
                            if (it.userLikeStatus == null) {
                                it.copy(userLikeStatus = "none")
                            } else {
                                it
                            }
                        }
                        comentariosViewModel.loadLikeStatuses(comentariosConDefaultStatus, userId, token)
                    }
                }
        }
    }

    LaunchedEffect(comentarioCreado) {
        if (comentarioCreado != null) {
            comentarioText = ""
            esSpoiler = false
            comentariosViewModel._comentarioCreado.value = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Comentarios",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (userId > 0) {
            OutlinedTextField(
                value = comentarioText,
                onValueChange = { comentarioText = it },
                placeholder = { Text("Escribe un comentario...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.Transparent,
                    cursorColor = Color.Green,
                    focusedBorderColor = Color.Green,
                    unfocusedBorderColor = Color.Gray
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (comentarioText.isNotBlank() && !isLoading) {
                            comentariosViewModel.createComentario(
                                userId = userId,
                                tmdbId = tmdbId,
                                tipo = tipo,
                                comentario = comentarioText,
                                esSpoiler = esSpoiler,
                                token = token
                            )
                        }
                    }
                ),
                trailingIcon = {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.Green,
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(
                            onClick = {
                                if (comentarioText.isNotBlank()) {
                                    comentariosViewModel.createComentario(
                                        userId = userId,
                                        tmdbId = tmdbId,
                                        tipo = tipo,
                                        comentario = comentarioText,
                                        esSpoiler = esSpoiler,
                                        token = token
                                    )
                                }
                            }
                        ) {
                            Icon(
                                Icons.Filled.Send,
                                contentDescription = "Enviar comentario",
                                tint = Color.Green
                            )
                        }
                    }
                },
                enabled = !isLoading
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = esSpoiler,
                    onCheckedChange = { esSpoiler = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.Green,
                        uncheckedColor = Color.Gray
                    ),
                    enabled = !isLoading
                )
                Text(
                    text = "Marcar como spoiler",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.DarkGray.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Inicia sesión para dejar comentarios",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Divider(
            color = Color.DarkGray,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        if (error != null) {
            Text(
                text = error ?: "Error desconocido",
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (isLoading && comentarios.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Green)
            }
        } else if (error != null && comentarios.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error al cargar comentarios",
                    color = Color.Red,
                    fontSize = 16.sp
                )
            }
        } else if (comentarios.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay comentarios. ¡Sé el primero en opinar!",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
            ) {
                items(
                    items = comentarios,
                    key = { it.id }
                ) { comentario ->
                    if (comentario.tmdbId == tmdbId && comentario.tipo == tipo) {
                        ComentarioItem(
                            comentario = comentario,
                            onDelete = {
                                if (userId == comentario.userId) {
                                    comentariosViewModel.deleteComentario(comentario.id, token)
                                }
                            },
                            onLike = {
                                if (userId > 0) {
                                    comentariosViewModel.likeComentario(userId, comentario.id, "like", token)
                                }
                            },
                            onDislike = {
                                if (userId > 0) {
                                    comentariosViewModel.likeComentario(userId, comentario.id, "dislike", token)
                                }
                            },
                            onShowRespuestas = {
                                selectedComentario = comentario
                                showRespuestasDialog = true
                            },
                            currentUserId = userId
                        )
                    }
                }
            }
        }
    }

    // Diálogo para mostrar respuestas
    if (showRespuestasDialog && selectedComentario != null) {
        SeccionRespuestas(
            comentario = selectedComentario!!,
            currentUserId = userId,
            respuestasViewModel = respuestasViewModel,
            onDismiss = {
                showRespuestasDialog = false
                selectedComentario = null
            }
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ComentarioItem(
    comentario: Comentario,
    onDelete: () -> Unit,
    onLike: () -> Unit,
    onDislike: () -> Unit,
    onShowRespuestas: () -> Unit,
    currentUserId: Int
) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()) }
    val outputFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    val formattedDate = try {
        val date = dateFormat.parse(comentario.createdAt)
        date?.let { outputFormat.format(it) } ?: "Fecha desconocida"
    } catch (e: Exception) {
        "Fecha desconocida"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (comentario.esSpoiler)
                Color.Red.copy(alpha = 0.1f)
            else if (comentario.usuario?.rol == "critico")
                Color(0xFF1A237E).copy(alpha = 0.2f)
            else
                Color.DarkGray.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar del usuario
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = comentario.usuario?.name ?: "Usuario",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        if (comentario.usuario?.rol == "critico") {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Crítico verificado",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Text(
                        text = formattedDate,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                if (currentUserId == comentario.userId) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar comentario",
                            tint = Color.Red
                        )
                    }
                }
            }

            if (comentario.esSpoiler) {
                Text(
                    text = "SPOILER",
                    color = Color.Red,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Text(
                text = comentario.comentario,
                fontSize = 16.sp,
                lineHeight = 24.sp
            )

            if (comentario.destacado) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Comentario destacado",
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic,
                    color = Color.Yellow
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sección de likes, dislikes y respuestas
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón de like
                BotonLike(
                    count = comentario.likesCount ?: 0,
                    isSelected = comentario.userLikeStatus == "like",
                    enabled = currentUserId > 0,
                    onClick = onLike
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Botón de dislike
                BotonDislike(
                    count = comentario.dislikesCount ?: 0,
                    isSelected = comentario.userLikeStatus == "dislike",
                    enabled = currentUserId > 0,
                    onClick = onDislike
                )

                Spacer(modifier = Modifier.weight(1f))

                // Botón para mostrar respuestas
                BotonRespuestas(
                    count = comentario.respuestasCount,
                    onClick = onShowRespuestas
                )
            }
        }
    }
}

@Composable
fun BotonLike(
    count: Int,
    isSelected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = if (isSelected) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                contentDescription = "Me gusta",
                tint = if (isSelected) Color.Green else if (enabled) Color.Gray else Color.Gray.copy(alpha = 0.5f)
            )
        }

        Text(
            text = count.toString(),
            fontSize = 14.sp,
            color = if (isSelected) Color.Green else Color.Gray,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
fun BotonDislike(
    count: Int,
    isSelected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = if (isSelected) Icons.Filled.ThumbDown else Icons.Outlined.ThumbDown,
                contentDescription = "No me gusta",
                tint = if (isSelected) Color.Red else if (enabled) Color.Gray else Color.Gray.copy(alpha = 0.5f)
            )
        }

        Text(
            text = count.toString(),
            fontSize = 14.sp,
            color = if (isSelected) Color.Red else Color.Gray,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
fun BotonRespuestas(
    count: Int,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.DarkGray.copy(alpha = 0.3f))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Comment,
            contentDescription = "Ver respuestas",
            tint = Color.Cyan,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = if (count > 0) count.toString() else "Responder",
            fontSize = 14.sp,
            color = Color.Cyan
        )
    }
}