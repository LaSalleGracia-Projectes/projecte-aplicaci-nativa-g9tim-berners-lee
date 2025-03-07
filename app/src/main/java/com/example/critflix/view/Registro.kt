package com.example.critflix.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.critflix.R
import com.example.critflix.Routes
import com.example.critflix.viewmodel.RegistrationState
import com.example.critflix.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Registro(navController: NavHostController) {
    var nombreUsuario by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val userViewModel: UserViewModel = viewModel()
    val registrationState by userViewModel.registrationState.collectAsState()

    // Observar el estado de registro
    LaunchedEffect(registrationState) {
        when (registrationState) {
            is RegistrationState.Success -> {
                // Navegar a la pantalla de inicio tras el registro exitoso
                navController.navigate(Routes.InicioSesion.route) {
                    popUpTo(Routes.Registro.route) { inclusive = true }
                }
            }
            is RegistrationState.Error -> {
                errorMessage = (registrationState as RegistrationState.Error).message
                showErrorDialog = true
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.letras_critflix),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedTextField(
            value = nombreUsuario,
            onValueChange = { nombreUsuario = it },
            placeholder = { Text("Nombre de Usuario") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.LightGray.copy(alpha = 0.2f),
                focusedBorderColor = Color(0xFF666666),
                unfocusedBorderColor = Color.LightGray
            )
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.LightGray.copy(alpha = 0.2f),
                focusedBorderColor = Color(0xFF666666),
                unfocusedBorderColor = Color.LightGray
            )
        )

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            placeholder = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.LightGray.copy(alpha = 0.2f),
                focusedBorderColor = Color(0xFF666666),
                unfocusedBorderColor = Color.LightGray
            )
        )

        OutlinedTextField(
            value = confirmarContrasena,
            onValueChange = { confirmarContrasena = it },
            placeholder = { Text("Confirmar Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.LightGray.copy(alpha = 0.2f),
                focusedBorderColor = Color(0xFF666666),
                unfocusedBorderColor = Color.LightGray
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                userViewModel.registerUser(
                    name = nombreUsuario,
                    email = email,
                    password = contrasena,
                    passwordConfirmation = confirmarContrasena
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Green
            ),
            enabled = registrationState !is RegistrationState.Loading
        ) {
            if (registrationState is RegistrationState.Loading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    "Registrarse",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Ya tienes cuenta?",
                fontSize = 14.sp,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "Iniciar Sesión",
                fontSize = 14.sp,
                color = Color(0xFF4A90E2),
                modifier = Modifier.clickable { navController.navigate(Routes.InicioSesion.route) }
            )
        }
    }

    // Diálogo de error
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                Button(
                    onClick = { showErrorDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Aceptar")
                }
            }
        )
    }
}