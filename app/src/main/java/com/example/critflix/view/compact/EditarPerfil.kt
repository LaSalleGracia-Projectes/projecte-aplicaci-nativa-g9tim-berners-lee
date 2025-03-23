package com.example.critflix.view.compact

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.critflix.R
import com.example.critflix.model.UserSessionManager
import com.example.critflix.viewmodel.EditarPerfilViewModel
import com.example.critflix.viewmodel.ProfileViewModel
import com.example.critflix.viewmodel.UpdateProfileState
import com.example.critflix.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPerfil(
    navController: NavController,
    profileViewModel: ProfileViewModel,
    userViewModel: UserViewModel,
    editarPerfilViewModel: EditarPerfilViewModel
) {
    val context = LocalContext.current
    val userSessionManager = remember { UserSessionManager(context) }
    val currentUser by profileViewModel.currentUser.observeAsState()
    val updateProfileState by editarPerfilViewModel.updateProfileState.observeAsState()

    // Variables para los campos editables
    var name by remember { mutableStateOf(currentUser?.name ?: "") }
    var description by remember { mutableStateOf(currentUser?.biografia ?: "") }

    // Reiniciar el estado al entrar a la pantalla
    LaunchedEffect(key1 = true) {
        editarPerfilViewModel.resetUpdateState()
    }

    // Actualizar campos si cambia el usuario actual
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            name = user.name ?: ""
            description = user.biografia ?: ""
        }
    }

    // Efecto para manejar el estado de actualización
    LaunchedEffect(updateProfileState) {
        when (updateProfileState) {
            is UpdateProfileState.Success -> {
                // Actualizar el usuario actual en el ProfileViewModel
                profileViewModel.setCurrentUser((updateProfileState as UpdateProfileState.Success).user)

                // Actualizar los datos en UserSessionManager
                val token = userSessionManager.getToken()
                if (token != null) {
                    userSessionManager.saveUserSession(token, (updateProfileState as UpdateProfileState.Success).user)
                }

                Toast.makeText(context, "Perfil actualizado con éxito", Toast.LENGTH_SHORT).show()
                navController.popBackStack()

                // Reiniciar el estado después de navegar
                editarPerfilViewModel.resetUpdateState()
            }
            is UpdateProfileState.Error -> {
                Toast.makeText(
                    context,
                    "Error: ${(updateProfileState as UpdateProfileState.Error).message}",
                    Toast.LENGTH_LONG
                ).show()
            }
            else -> {}
        }
    }

    // Limpiar el estado al salir de la pantalla
    DisposableEffect(Unit) {
        onDispose {
            editarPerfilViewModel.resetUpdateState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil") },
                navigationIcon = {
                    IconButton(onClick = {
                        editarPerfilViewModel.resetUpdateState() // Resetear antes de navegar
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Contenido principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color.LightGray)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.user),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Campo para el nombre
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo para la descripción
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Botón para guardar los cambios
                Button(
                    onClick = {
                        val userId = userSessionManager.getUserId()
                        val token = userSessionManager.getToken()

                        if (userId != -1 && token != null) {
                            editarPerfilViewModel.updateUserProfile(userId, token, name, description)
                        } else {
                            Toast.makeText(context, "Error: Sesión no válida", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = updateProfileState !is UpdateProfileState.Loading
                ) {
                    if (updateProfileState is UpdateProfileState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Guardar Cambios")
                    }
                }
            }

            if (updateProfileState is UpdateProfileState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}