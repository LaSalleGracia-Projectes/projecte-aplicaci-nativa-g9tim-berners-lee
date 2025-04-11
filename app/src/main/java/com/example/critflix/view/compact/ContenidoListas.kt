package com.example.critflix.view.compact

import android.util.Log
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
import androidx.navigation.NavController
import com.example.critflix.viewmodel.ContenidoListaViewModel
import com.example.critflix.viewmodel.ListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContenidoListas(
    navController: NavController,
    listViewModel: ListViewModel,
    id: String,
    contenidoListaViewModel: ContenidoListaViewModel
) {
    val listas by listViewModel.listas.observeAsState(emptyList())
    val lista = listas.find { it.id == id }

    LaunchedEffect(id) {
        Log.d("ContenidoListas", "Buscando lista con ID: $id")
        Log.d("ContenidoListas", "Listas disponibles: ${listas.map { "${it.id}:${it.name}" }}")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = lista?.name ?: "Lista",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver atrás",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Contenido de la lista: ${lista?.name ?: "Desconocida"}",
                color = Color.White
            )
        }
    }
}