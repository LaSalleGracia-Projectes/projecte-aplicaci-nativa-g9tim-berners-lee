package com.example.critflix.view.compact

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

    var name by remember { mutableStateOf(currentUser?.name ?: "") }
    var description by remember { mutableStateOf(currentUser?.biografia ?: "") }

    LaunchedEffect(key1 = true) {
        editarPerfilViewModel.resetUpdateState()
    }

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            name = user.name ?: ""
            description = user.biografia ?: ""
        }
    }

    LaunchedEffect(updateProfileState) {
        when (updateProfileState) {
            is UpdateProfileState.Success -> {
                profileViewModel.setCurrentUser((updateProfileState as UpdateProfileState.Success).user)
                val token = userSessionManager.getToken()
                if (token != null) {
                    userSessionManager.saveUserSession(token, (updateProfileState as UpdateProfileState.Success).user)
                }
                Toast.makeText(context, "Perfil actualizado con éxito", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
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

    DisposableEffect(Unit) {
        onDispose {
            editarPerfilViewModel.resetUpdateState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = {
                        editarPerfilViewModel.resetUpdateState()
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color.Black)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción", color = Color.White) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    maxLines = 5,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green, contentColor = Color.Black)
                ) {
                    if (updateProfileState is UpdateProfileState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                    } else {
                        Text("Guardar Cambios")
                    }
                }
            }
        }
    }
}
