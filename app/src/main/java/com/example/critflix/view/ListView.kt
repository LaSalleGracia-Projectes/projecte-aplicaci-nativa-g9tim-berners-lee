package com.example.critflix.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.critflix.model.PelisPopulares
import com.example.critflix.viewmodel.APIViewModel

@Composable
fun ListView(navController: NavHostController, apiViewModel: APIViewModel) {

    val peliculas by apiViewModel.pelis.observeAsState(emptyList())
    var tabSeleccionado by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        apiViewModel.getPelis(totalMoviesNeeded = 30)
    }

    Scaffold(
        topBar = { TopBarPeliculas(tabSeleccionado) { tabSeleccionado = it } },
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        ContenidoPrincipal(
            peliculas = peliculas,
            paddingValues = padding
        )
    }
}

@Composable
private fun TopBarPeliculas(tabSeleccionado: Int, onTabSelected: (Int) -> Unit) {

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mis listas",
                style = MaterialTheme.typography.titleLarge
            )
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Editar"
            )
        }

        TabRow(
            selectedTabIndex = tabSeleccionado,
            containerColor = MaterialTheme.colorScheme.background
        ) {
            Tab(
                selected = tabSeleccionado == 0,
                onClick = { onTabSelected(0) },
                text = { Text("FAVORITOS") }
            )
            Tab(
                selected = tabSeleccionado == 1,
                onClick = { onTabSelected(1) },
                text = { Text("CRITILISTAS") }
            )
        }
    }
}

@Composable
fun ContenidoPrincipal(peliculas: List<PelisPopulares>, paddingValues: PaddingValues) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        ContadorYBotonAnadir(cantidadPeliculas = peliculas.size)

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(peliculas) { pelicula ->
                TarjetaPelicula(pelicula = pelicula)
            }
        }
    }
}

@Composable
fun ContadorYBotonAnadir(cantidadPeliculas: Int) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("$cantidadPeliculas/100 Títulos")
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Añadir"
            )
            Text("Añadir")
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun TarjetaPelicula(pelicula: PelisPopulares) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        GlideImage(
            model = "https://image.tmdb.org/t/p/w185${pelicula.poster_path}",
            contentDescription = pelicula.title,
            modifier = Modifier
                .width(100.dp)
                .height(150.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = pelicula.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Row {
                    IconButton(onClick = { /* Marcar favorito */ }) {
                        Icon(Icons.Default.FavoriteBorder, "Favorito")
                    }
                    IconButton(onClick = { /* Mostrar opciones */ }) {
                        Icon(Icons.Default.MoreVert, "Más opciones")
                    }
                }
            }

            Text(
                text = "Fecha: ${pelicula.release_date} | ${pelicula.original_language.uppercase()}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}