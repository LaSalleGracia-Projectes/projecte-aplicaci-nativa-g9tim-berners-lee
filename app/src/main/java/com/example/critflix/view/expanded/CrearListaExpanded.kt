package com.example.critflix.view.expanded

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.critflix.model.UserSessionManager
import com.example.critflix.viewmodel.CreateListState
import com.example.critflix.viewmodel.ListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearListaExpanded(
    navController: NavController,
    listViewModel: ListViewModel,
    listId: String? = null
) {
    val listas by listViewModel.listas.observeAsState(emptyList())
    val context = LocalContext.current
    val userSessionManager = remember { UserSessionManager(context) }
    val userId = userSessionManager.getUserId()
    val token = userSessionManager.getToken() ?: ""
    val listaActual = listId?.let { id -> listas.find { it.id == id } }
    var nombreLista by remember { mutableStateOf(listaActual?.name ?: "") }
    var isFormValid by remember { mutableStateOf(false) }
    val createListState by listViewModel.createListState.observeAsState()
    val isEditMode = listId != null

    LaunchedEffect(nombreLista) {
        isFormValid = nombreLista.isNotBlank() && nombreLista.length <= 100
    }

    LaunchedEffect(createListState) {
        when (createListState) {
            is CreateListState.Success -> {
                Toast.makeText(
                    context,
                    if (isEditMode) "Lista renombrada exitosamente" else "Lista creada exitosamente",
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Black,
            contentColor = Color.White,
            topBar = {
                TopAppBar(
                    title = { Text(if (isEditMode) "Renombrar Lista" else "Crear Lista", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Black,
                        navigationIconContentColor = Color.White,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
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
                        label = { Text("Nombre de la lista", color = Color.White) },
                        textStyle = TextStyle(color = Color.White),
                        modifier = Modifier.fillMaxWidth(),
                        isError = nombreLista.isNotBlank() && nombreLista.length > 100,
                        supportingText = {
                            Text(
                                text = "${nombreLista.length}/100",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End,
                                color = Color.White
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.Gray,
                            errorBorderColor = Color.Red,
                            cursorColor = Color.White,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.Gray
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (isFormValid && userId > 0) {
                                if (isEditMode) {
                                    listViewModel.renameList(listId!!, nombreLista, token)
                                } else {
                                    listViewModel.createNewList(nombreLista, userId, token)
                                }
                            } else if (userId <= 0) {
                                Toast.makeText(
                                    context,
                                    "Debes iniciar sesión para ${if (isEditMode) "renombrar" else "crear"} una lista",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isFormValid && createListState !is CreateListState.Loading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1AD71F),
                            contentColor = Color.White
                        )
                    ) {
                        if (createListState is CreateListState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(if (isEditMode) "Renombrar Lista" else "Crear Lista")
                        }
                    }
                }
            }
        }
    }
}