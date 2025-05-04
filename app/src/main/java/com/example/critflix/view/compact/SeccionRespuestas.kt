package com.example.critflix.view.compact

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.critflix.model.Comentario
import com.example.critflix.model.RespuestaComentario
import com.example.critflix.model.UserSessionManager
import com.example.critflix.viewmodel.RespuestasViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeccionRespuestas(
    comentario: Comentario,
    currentUserId: Int,
    respuestasViewModel: RespuestasViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val userSessionManager = remember { UserSessionManager(context) }
    val token = userSessionManager.getToken() ?: ""

    var respuestaText by remember { mutableStateOf("") }
    var esSpoiler by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val isLoading by respuestasViewModel.isLoading.observeAsState(false)
    val error by respuestasViewModel.error.observeAsState()
    val respuestasMap by respuestasViewModel.respuestas.observeAsState(mapOf())
    val respuestaCreada by respuestasViewModel.respuestaCreada.observeAsState()

    val respuestas = respuestasMap[comentario.id] ?: emptyList()

    LaunchedEffect(comentario.id) {
        respuestasViewModel.getRespuestasByComentarioId(comentario.id, token)
    }

    LaunchedEffect(respuestaCreada) {
        if (respuestaCreada != null) {
            respuestaText = ""
            esSpoiler = false
            respuestasViewModel.clearRespuestaCreada()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp),
            color = Color.Black,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Título del diálogo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Respuestas",
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color.Gray
                        )
                    }
                }

                Divider(
                    color = Color.DarkGray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Comentario original
                ComentarioOriginal(comentario = comentario)

                Divider(
                    color = Color.DarkGray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Campo para escribir respuesta
                if (currentUserId > 0) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.DarkGray.copy(alpha = 0.2f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            OutlinedTextField(
                                value = respuestaText,
                                onValueChange = { respuestaText = it },
                                placeholder = { Text("Escribe una respuesta...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    containerColor = Color.Transparent,
                                    cursorColor = Color.Green,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color.Green,
                                    unfocusedBorderColor = Color.Gray
                                ),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                                keyboardActions = KeyboardActions(
                                    onSend = {
                                        if (respuestaText.isNotBlank() && !isLoading && currentUserId > 0) {
                                            respuestasViewModel.createRespuesta(
                                                comentarioId = comentario.id,
                                                userId = currentUserId,
                                                respuesta = respuestaText,
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
                                                if (respuestaText.isNotBlank() && currentUserId > 0) {
                                                    respuestasViewModel.createRespuesta(
                                                        comentarioId = comentario.id,
                                                        userId = currentUserId,
                                                        respuesta = respuestaText,
                                                        esSpoiler = esSpoiler,
                                                        token = token
                                                    )
                                                }
                                            }
                                        ) {
                                            Icon(
                                                Icons.Default.Send,
                                                contentDescription = "Enviar respuesta",
                                                tint = Color.Green
                                            )
                                        }
                                    }
                                },
                                enabled = !isLoading && currentUserId > 0,
                                maxLines = 3,
                                singleLine = false
                            )

                            // Checkbox para marcar como spoiler
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 4.dp)
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
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
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
                                text = "Inicia sesión para dejar respuestas",
                                fontSize = 16.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Lista de respuestas
                if (isLoading && respuestas.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.Green,
                            modifier = Modifier.size(32.dp),
                            strokeWidth = 2.dp
                        )
                    }
                } else if (error != null && respuestas.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = error ?: "Error al cargar respuestas",
                            color = Color.Red,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                } else if (respuestas.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.QuestionAnswer,
                                contentDescription = "Sin respuestas",
                                tint = Color.Gray,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No hay respuestas aún. ¡Sé el primero en responder!",
                                color = Color.Gray,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(respuestas) { respuesta ->
                            RespuestaItem(
                                respuesta = respuesta,
                                onDelete = {
                                    if (currentUserId == respuesta.userId) {
                                        respuestasViewModel.deleteRespuesta(respuesta.id, comentario.id, token)
                                    }
                                },
                                currentUserId = currentUserId
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ComentarioOriginal(comentario: Comentario) {
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
                Color.DarkGray.copy(alpha = 0.1f)
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
                        .size(36.dp)
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
                            color = Color.White,
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
                color = Color.White,
                lineHeight = 24.sp
            )
        }
    }
}

@Composable
fun RespuestaItem(
    respuesta: RespuestaComentario,
    onDelete: () -> Unit,
    currentUserId: Int
) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()) }
    val outputFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    val formattedDate = try {
        val date = dateFormat.parse(respuesta.createdAt)
        date?.let { outputFormat.format(it) } ?: "Fecha desconocida"
    } catch (e: Exception) {
        "Fecha desconocida"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (respuesta.esSpoiler)
                Color.Red.copy(alpha = 0.1f)
            else
                Color.DarkGray.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
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
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = respuesta.usuario?.name ?: "Usuario",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        if (respuesta.usuario?.rol == "critico") {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Crítico verificado",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text = formattedDate,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }

                if (currentUserId == respuesta.userId) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar respuesta",
                            tint = Color.Red
                        )
                    }
                }
            }

            if (respuesta.esSpoiler) {
                Text(
                    text = "SPOILER",
                    color = Color.Red,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Text(
                text = respuesta.respuesta,
                fontSize = 14.sp,
                color = Color.White,
                lineHeight = 20.sp
            )
        }
    }
}