package com.example.critflix.view.compact

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.critflix.model.SolicitudCritico
import com.example.critflix.model.UserSessionManager
import com.example.critflix.viewmodel.SolicitudCriticoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SolicitudCritico(navController: NavController, solicitudCriticoViewModel: SolicitudCriticoViewModel) {
    val backgroundColor = Color.Black
    val textColor = Color.White
    val accentColor = Color(0xFF00FF0B)
    val errorColor = Color(0xFFFF3333)

    val context = LocalContext.current
    val userSessionManager = remember { UserSessionManager(context) }
    val userId = userSessionManager.getUserId()
    val scope = rememberCoroutineScope()
    val token = userSessionManager.getToken() ?: ""

    var nombre by rememberSaveable { mutableStateOf("") }
    var apellido by rememberSaveable { mutableStateOf("") }
    var edad by rememberSaveable { mutableStateOf("") }
    var motivo by rememberSaveable { mutableStateOf("") }

    var nombreError by remember { mutableStateOf(false) }
    var apellidoError by remember { mutableStateOf(false) }
    var edadError by remember { mutableStateOf(false) }
    var motivoError by remember { mutableStateOf(false) }

    val uiState by solicitudCriticoViewModel.uiState.collectAsStateWithLifecycle()

    var showSuccessMessage by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            showSuccessMessage = true
            delay(3000)
            showSuccessMessage = false
        }
    }

    LaunchedEffect(userId) {
        if (userId > 0) {
            solicitudCriticoViewModel.getUserSolicitudes(userId, token)
        }
    }

    val pendingSolicitud = uiState.solicitudList.find { it.estado == "pendiente" }
    val approvedSolicitud = uiState.solicitudList.find { it.estado == "aprobada" }
    val rejectedSolicitudes = uiState.solicitudList.filter { it.estado == "rechazada" }
    val lastRejectedSolicitud = rejectedSolicitudes.maxByOrNull { it.updated_at }

    val showForm = pendingSolicitud == null && approvedSolicitud == null

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    modifier = Modifier
                        .clickable { navController.popBackStack() }
                        .size(24.dp),
                    tint = textColor
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Solicitud de crítico",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }

            if (showSuccessMessage) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = accentColor.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Éxito",
                            tint = accentColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Solicitud enviada con éxito",
                            color = accentColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = accentColor)
                }
            }
            else if (uiState.errorMessage != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = errorColor.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Error",
                            tint = errorColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = uiState.errorMessage!!,
                            color = errorColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            else {
                when {
                    approvedSolicitud != null -> {
                        SolicitudStatusCard(
                            solicitud = approvedSolicitud,
                            status = "aprobada",
                            accentColor = accentColor,
                            textColor = textColor,
                            errorColor = errorColor
                        )
                    }
                    pendingSolicitud != null -> {
                        SolicitudStatusCard(
                            solicitud = pendingSolicitud,
                            status = "pendiente",
                            accentColor = accentColor,
                            textColor = textColor,
                            errorColor = errorColor
                        )
                    }
                    lastRejectedSolicitud != null && !showForm -> {
                        SolicitudStatusCard(
                            solicitud = lastRejectedSolicitud,
                            status = "rechazada",
                            accentColor = accentColor,
                            textColor = textColor,
                            errorColor = errorColor
                        )

                        Button(
                            onClick = {
                                solicitudCriticoViewModel.resetState()
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = accentColor
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                                .height(50.dp)
                        ) {
                            Text(
                                "Enviar nueva solicitud",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    showForm -> {
                        FormFields(
                            nombre = nombre,
                            onNombreChange = { nombre = it; nombreError = it.isBlank() },
                            nombreError = nombreError,
                            apellido = apellido,
                            onApellidoChange = { apellido = it; apellidoError = it.isBlank() },
                            apellidoError = apellidoError,
                            edad = edad,
                            onEdadChange = {
                                edad = it.filter { char -> char.isDigit() }
                                edadError = it.isBlank() || it.toIntOrNull() == null || it.toIntOrNull() !in 18..120
                            },
                            edadError = edadError,
                            motivo = motivo,
                            onMotivoChange = {
                                motivo = it
                                motivoError = it.length < 20
                            },
                            motivoError = motivoError,
                            accentColor = accentColor,
                            textColor = textColor,
                            errorColor = errorColor
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick = {
                                    nombreError = nombre.isBlank()
                                    apellidoError = apellido.isBlank()
                                    edadError = edad.isBlank() || edad.toIntOrNull() == null || edad.toIntOrNull() !in 18..120
                                    motivoError = motivo.length < 20

                                    if (!nombreError && !apellidoError && !edadError && !motivoError) {
                                        scope.launch {
                                            solicitudCriticoViewModel.createSolicitud(
                                                userId = userId,
                                                nombre = nombre,
                                                apellido = apellido,
                                                edad = edad.toInt(),
                                                motivo = motivo,
                                                token = token
                                            )

                                            delay(500)
                                            solicitudCriticoViewModel.getUserSolicitudes(userId, token)
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = accentColor
                                ),
                                modifier = Modifier
                                    .height(50.dp)
                                    .width(200.dp)
                            ) {
                                Text(
                                    "Enviar solicitud",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FormFields(
    nombre: String,
    onNombreChange: (String) -> Unit,
    nombreError: Boolean,
    apellido: String,
    onApellidoChange: (String) -> Unit,
    apellidoError: Boolean,
    edad: String,
    onEdadChange: (String) -> Unit,
    edadError: Boolean,
    motivo: String,
    onMotivoChange: (String) -> Unit,
    motivoError: Boolean,
    accentColor: Color,
    textColor: Color,
    errorColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.LightGray.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Datos personales",
                style = MaterialTheme.typography.titleMedium,
                color = accentColor,
                fontWeight = FontWeight.Bold
            )

            // Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = onNombreChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray.copy(alpha = 0.2f)),
                placeholder = { Text("Nombre", color = Color.Gray) },
                isError = nombreError,
                supportingText = {
                    if (nombreError) {
                        Text("El nombre es requerido", color = errorColor)
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    cursorColor = accentColor,
                    focusedIndicatorColor = accentColor,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    errorIndicatorColor = errorColor,
                    errorCursorColor = errorColor
                )
            )

            // Apellido
            OutlinedTextField(
                value = apellido,
                onValueChange = onApellidoChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray.copy(alpha = 0.2f)),
                placeholder = { Text("Apellido", color = Color.Gray) },
                isError = apellidoError,
                supportingText = {
                    if (apellidoError) {
                        Text("El apellido es requerido", color = errorColor)
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    cursorColor = accentColor,
                    focusedIndicatorColor = accentColor,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    errorIndicatorColor = errorColor,
                    errorCursorColor = errorColor
                )
            )

            // Edad
            OutlinedTextField(
                value = edad,
                onValueChange = onEdadChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray.copy(alpha = 0.2f)),
                placeholder = { Text("Edad", color = Color.Gray) },
                isError = edadError,
                supportingText = {
                    if (edadError) {
                        Text("Debes ser mayor de edad", color = errorColor)
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    cursorColor = accentColor,
                    focusedIndicatorColor = accentColor,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    errorIndicatorColor = errorColor,
                    errorCursorColor = errorColor
                )
            )
        }
    }

    // Motivo
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.LightGray.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Motivo de la solicitud",
                style = MaterialTheme.typography.titleMedium,
                color = accentColor,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = motivo,
                onValueChange = onMotivoChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray.copy(alpha = 0.2f)),
                placeholder = { Text("Explica por qué quieres ser crítico (mínimo 20 caracteres)", color = Color.Gray) },
                isError = motivoError,
                supportingText = {
                    if (motivoError) {
                        Text("El motivo debe tener al menos 20 caracteres", color = errorColor)
                    }
                },
                maxLines = 5,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    cursorColor = accentColor,
                    focusedIndicatorColor = accentColor,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    errorIndicatorColor = errorColor,
                    errorCursorColor = errorColor
                )
            )
        }
    }
}

@Composable
fun SolicitudStatusCard(
    solicitud: SolicitudCritico,
    status: String,
    accentColor: Color,
    textColor: Color,
    errorColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.LightGray.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (status) {
                "aprobada" -> {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Aprobada",
                        tint = accentColor,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "¡Felicidades!",
                        color = accentColor,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tu solicitud para ser crítico ha sido aprobada. Ya puedes disfrutar de los beneficios de ser un crítico verificado.",
                        color = textColor,
                        textAlign = TextAlign.Center
                    )
                }
                "pendiente" -> {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Pendiente",
                        tint = Color.Yellow,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "Solicitud en revisión",
                        color = Color.Yellow,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tu solicitud está siendo revisada por nuestro equipo. Te notificaremos cuando haya novedades.",
                        color = textColor,
                        textAlign = TextAlign.Center
                    )
                }
                "rechazada" -> {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Rechazada",
                        tint = errorColor,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "Solicitud rechazada",
                        color = errorColor,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Lo sentimos, tu solicitud para ser crítico ha sido rechazada. Puedes intentarlo nuevamente.",
                        color = textColor,
                        textAlign = TextAlign.Center
                    )
                }
            }

            SolicitudDetails(solicitud, textColor, accentColor)
        }
    }
}

@Composable
fun SolicitudDetails(
    solicitud: SolicitudCritico,
    textColor: Color,
    accentColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Divider(color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Detalles de la solicitud",
            color = accentColor,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        DetailRow("Nombre", "${solicitud.nombre} ${solicitud.apellido}", textColor)
        DetailRow("Edad", "${solicitud.edad} años", textColor)
        DetailRow("Estado", solicitud.estado, textColor)
        DetailRow("Fecha de solicitud", solicitud.created_at.substring(0, 10), textColor)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Motivo",
            color = accentColor,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = solicitud.motivo,
            color = textColor
        )
    }
}

@Composable
fun DetailRow(label: String, value: String, textColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            color = textColor
        )
    }
}