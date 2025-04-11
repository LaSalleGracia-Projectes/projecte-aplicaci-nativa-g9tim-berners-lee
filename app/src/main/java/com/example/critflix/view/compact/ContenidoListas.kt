package com.example.critflix.view.compact

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    id: Int,
    contenidoListaViewModel: ContenidoListaViewModel
) {
    val lista = listViewModel.listas.value?.find { it.id == id.toString() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = lista?.name ?: "Lista") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver atrás"
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black)
        ) {
            // Aquí irá el contenido de la lista
        }
    }
}
