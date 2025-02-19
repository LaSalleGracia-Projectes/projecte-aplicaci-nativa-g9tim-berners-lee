package com.example.critflix.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.example.critflix.R
import com.example.critflix.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Registro(navController: NavHostController) {
    var nombreUsuario by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.critiflix),
            contentDescription = "Logo",
            modifier = Modifier
                .size(150.dp)
                .padding(top = 30.dp, bottom = 40.dp)
                .align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Username field
        OutlinedTextField(
            value = nombreUsuario,
            onValueChange = { nombreUsuario = it },
            placeholder = { Text("Nombre de Usuario") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(4.dp)),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.LightGray.copy(alpha = 0.3f)
            )
        )

        // Email field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(4.dp)),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.LightGray.copy(alpha = 0.3f)
            )
        )

        // Password field
        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            placeholder = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(4.dp)),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.LightGray.copy(alpha = 0.3f)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Register button
        Button(
            onClick = { navController.navigate(Routes.Home.route) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xBCB2A8A8)
            )
        ) {
            Text("Registrarse", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Ya tienes cuenta?", fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "Iniciar Sesión",
                fontSize = 14.sp,
                color = Color.Blue,
                modifier = Modifier.clickable { }
            )
        }
    }
}