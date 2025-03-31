package com.example.critflix.view.compact

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.critflix.model.PelisPopulares
import com.example.critflix.view.util.ActorCarousel
import com.example.critflix.viewmodel.APIViewModel
import com.example.critflix.viewmodel.GenresViewModel
import com.example.critflix.viewmodel.RepartoViewModel

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun InfoPelis(navController: NavHostController, apiViewModel: APIViewModel, id: Int, genresViewModel: GenresViewModel, repartoViewModel: RepartoViewModel) {
    val peliculas: List<PelisPopulares> by apiViewModel.pelis.observeAsState(emptyList())
    val pelicula = peliculas.find { it.id == id }
    var isFavorite by remember { mutableStateOf(false) }

    LaunchedEffect(id) {
        repartoViewModel.getMovieCredits(id)
    }
    val movieCredits by repartoViewModel.movieCredits.observeAsState()
    val isLoading by repartoViewModel.isLoading.observeAsState(initial = false)
    val error by repartoViewModel.error.observeAsState()
    val genreMap by genresViewModel.genreMap.observeAsState(emptyMap())
    val genresLoading by genresViewModel.loading.observeAsState(initial = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles de la película") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { isFavorite = !isFavorite }) {
                        Icon(
                            if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (isFavorite) Color.Red else Color.Gray
                        )
                    }
                    IconButton(onClick = { /* Compartir */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Compartir")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (pelicula != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Imagen de portada
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                ) {
                    GlideImage(
                        model = "https://image.tmdb.org/t/p/w500${pelicula.backdrop_path}",
                        contentDescription = pelicula.title,
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                    )
                    // Calificación
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = "${pelicula.vote_average}",
                            modifier = Modifier.padding(8.dp),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Información principal
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = pelicula.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Popularidad: ${pelicula.popularity}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Fecha: ${pelicula.release_date}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    // Categorías/Géneros
                    Text(
                        text = "Categorías",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (genresLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            items(pelicula.genre_ids) { genreId ->
                                genreMap[genreId]?.let { genreName ->
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = genreName,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Sinopsis
                    Text(
                        text = "Sinopsis",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = if (pelicula.overview.isNotEmpty()) pelicula.overview else "No hay sinopsis que mostrar",
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Sección de reparto
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (error != null) {
                        Text(
                            text = "Error al cargar el reparto",
                            color = Color.Red,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    } else {
                        movieCredits?.let { credits ->
                            val mainCast = credits.cast.take(10)
                            if (mainCast.isNotEmpty()) {
                                ActorCarousel(actores = mainCast)
                            } else {
                                Text(
                                    text = "No hay información de reparto disponible",
                                    modifier = Modifier.padding(vertical = 16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botones de acción
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { /* Ver trailer */ },
                            modifier = Modifier.weight(1f).padding(end = 8.dp)
                        ) {
                            Text("Ver trailer")
                        }
                        Button(
                            onClick = { /* Añadir a lista */ },
                            modifier = Modifier.weight(1f).padding(start = 8.dp)
                        ) {
                            Text("Añadir a lista")
                        }
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}