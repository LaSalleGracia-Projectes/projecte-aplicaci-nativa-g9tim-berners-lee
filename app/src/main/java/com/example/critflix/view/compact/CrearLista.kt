package com.example.critflix.view.compact

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.critflix.model.UserSessionManager
import com.example.critflix.viewmodel.CreateListState
import com.example.critflix.viewmodel.ListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearLista(navController: NavController, listViewModel: ListViewModel) {
    val context = LocalContext.current
    val userSessionManager = remember { UserSessionManager(context) }
    val userId = userSessionManager.getUserId()
    val token = userSessionManager.getToken() ?: ""
    var nombreLista by remember { mutableStateOf("") }
    var isFormValid by remember { mutableStateOf(false) }
    val createListState by listViewModel.createListState.observeAsState()

    // Validar formulario cuando cambia el nombre
    LaunchedEffect(nombreLista) {
        isFormValid = nombreLista.isNotBlank() && nombreLista.length <= 100
    }

    // Cambios en el estado de creación
    LaunchedEffect(createListState) {
        when (createListState) {
            is CreateListState.Success -> {
                Toast.makeText(
                    context,
                    "Lista creada exitosamente",
                    Toast.LENGTH_SHORT
                ).show()
                navController.popBackStack()
            }
            is CreateListState.Error -> {
                Toast.makeText(
                    context,
                    (createListState as CreateListState.Error).message,
                    Toast.LENGTH_LONG
                ).show()
            }
            else -> { }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            listViewModel._createListState.value = CreateListState.Idle
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Lista") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = nombreLista,
                    onValueChange = {
                        if (it.length <= 100) {
                            nombreLista = it
                        }
                    },
                    label = { Text("Nombre de la lista") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nombreLista.isNotBlank() && nombreLista.length > 100,
                    supportingText = {
                        Text(
                            text = "${nombreLista.length}/100",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End,
                        )
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (isFormValid && userId > 0) {
                            listViewModel.createNewList(nombreLista, userId, token)
                        } else if (userId <= 0) {
                            Toast.makeText(
                                context,
                                "Debes iniciar sesión para crear una lista",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isFormValid && createListState !is CreateListState.Loading
                ) {
                    if (createListState is CreateListState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Crear Lista")
                    }
                }
            }
        }
    }
}