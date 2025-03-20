package com.example.critflix.view.compact

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.critflix.R
import com.example.critflix.model.User
import com.example.critflix.viewmodel.ProfileViewModel
import com.example.critflix.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPerfil(
    navController: NavController,
    profileViewModel: ProfileViewModel,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val currentUser by profileViewModel.currentUser.observeAsState()

    // Variables para los campos editables
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Estado del perfil
    val profileState by profileViewModel.profileState.observeAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil") },
                actions = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
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
                    /* Funcionalidad */
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Guardar Cambios")
            }
        }
    }
}

