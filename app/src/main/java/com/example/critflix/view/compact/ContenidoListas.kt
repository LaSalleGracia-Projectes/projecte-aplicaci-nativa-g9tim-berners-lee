package com.example.critflix.view.compact

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.critflix.model.ContenidoLista
import com.example.critflix.model.PelisPopulares
import com.example.critflix.model.SeriesPopulares
import com.example.critflix.model.UserSessionManager
import com.example.critflix.nav.Routes
import com.example.critflix.viewmodel.APIViewModel
import com.example.critflix.viewmodel.ContenidoListaViewModel
import com.example.critflix.viewmodel.ContentState
import com.example.critflix.viewmodel.ListViewModel
import com.example.critflix.viewmodel.SeriesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContenidoListas(
    navController: NavController,
    listViewModel: ListViewModel,
    apiViewModel: APIViewModel,
    seriesViewModel: SeriesViewModel,
    id: String,
    contenidoListaViewModel: ContenidoListaViewModel
) {
    val listas by listViewModel.listas.observeAsState(emptyList())
    val lista = listas.find { it.id == id }

    val contentItems by contenidoListaViewModel.contentItems.observeAsState(emptyList())
    val contentState by contenidoListaViewModel.contentState.observeAsState(ContentState.Idle)

    val movies by contenidoListaViewModel.movies.observeAsState()
    val series by contenidoListaViewModel.series.observeAsState()

    val context = LocalContext.current
    val userSessionManager = remember { UserSessionManager(context) }
    val token = userSessionManager.getToken() ?: ""

    LaunchedEffect(id) {
        Log.d("ContenidoListas", "Cargando contenido para lista ID: $id")
        contenidoListaViewModel.loadListContent(id, token)
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
                actions = {
                    IconButton(onClick = {
                        navController.navigate("${Routes.Busqueda.route}/$id")
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Añadir contenido",
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(innerPadding)
        ) {
            when (contentState) {
                ContentState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                is ContentState.Error -> {
                    val errorState = contentState as ContentState.Error
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = errorState.message,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { contenidoListaViewModel.loadListContent(id, token) }) {
                            Text("Reintentar")
                        }
                    }
                }

                else -> {
                    if (contentItems.isEmpty()) {
                        // Lista vacía
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Movie,
                                contentDescription = "Lista vacía",
                                tint = Color.Gray,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Esta lista está vacía",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Añade películas o series utilizando el botón +",
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(onClick = { /* Navegar a buscar contenido */ }) {
                                Text("Añadir contenido")
                            }
                        }
                    } else {
                        // Mostrar contenido de la lista
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            items(contentItems) { content ->
                                ContentListItem(
                                    navController = navController,
                                    content = content,
                                    movies = movies,
                                    series = series,
                                    onRemove = {
                                        contenidoListaViewModel.removeContentFromList(content.id, content.tmdb_id, token)
                                        Toast.makeText(context, "Contenido eliminado de la lista", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// TODO: Navegacion segun el tipo de contenido
// TODO: Agregar logica al ViewModel
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ContentListItem(
    navController: NavController,
    content: ContenidoLista,
    movies: Map<Int, PelisPopulares>?,
    series: Map<Int, SeriesPopulares>?,
    onRemove: () -> Unit
) {
    val baseImageUrl = "https://image.tmdb.org/t/p/w185"

    val details = when (content.tipo) {
        "pelicula" -> movies?.get(content.tmdb_id)
        "serie" -> series?.get(content.tmdb_id)
        else -> null
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { 
                when (content.tipo) {
                    "pelicula" -> navController.navigate(Routes.InfoPelis.createRoute(content.tmdb_id))
                    "serie" -> navController.navigate(Routes.InfoSeries.createRoute(content.tmdb_id))
                }
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val posterPath = when {
                content.tipo == "pelicula" -> (details as? PelisPopulares)?.poster_path
                content.tipo == "serie" -> (details as? SeriesPopulares)?.poster_path
                else -> null
            }

            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(100.dp, 150.dp)
            ) {
                if (!posterPath.isNullOrEmpty()) {
                    GlideImage(
                        model = "$baseImageUrl$posterPath",
                        contentDescription = "Poster",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.DarkGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (content.tipo == "pelicula") Icons.Default.Movie else Icons.Default.Tv,
                            contentDescription = content.tipo,
                            modifier = Modifier.size(40.dp),
                            tint = Color.White
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                val title = when {
                    content.tipo == "pelicula" -> (details as? PelisPopulares)?.title
                    content.tipo == "serie" -> (details as? SeriesPopulares)?.name
                    else -> null
                }

                Text(
                    text = title ?: "ID: ${content.tmdb_id}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                val releaseDate = when {
                    content.tipo == "pelicula" -> (details as? PelisPopulares)?.release_date?.take(4)
                    content.tipo == "serie" -> (details as? SeriesPopulares)?.first_air_date?.take(4)
                    else -> null
                }

                if (!releaseDate.isNullOrEmpty()) {
                    Text(
                        text = releaseDate,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                val rating = when {
                    content.tipo == "pelicula" -> (details as? PelisPopulares)?.vote_average
                    content.tipo == "serie" -> (details as? SeriesPopulares)?.vote_average
                    else -> null
                }

                if (rating != null) {
                    val ratingColor = when {
                        rating >= 7 -> Color(0xFF4CAF50)
                        rating >= 5 -> Color(0xFFFFA000)
                        else -> Color(0xFFF44336)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "★ ${String.format("%.1f", rating)}/10",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ratingColor
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Mostrar fecha de agregado
                /*Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Añadido: ${content.fecha_agregado}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }*/

                Spacer(modifier = Modifier.height(8.dp))

                // Mostrar tipo de contenido
                SuggestionChip(
                    onClick = {  },
                    label = {
                        Text(
                            text = if (content.tipo == "pelicula") "Película" else "Serie",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                        labelColor = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier.height(24.dp),
                    icon = {
                        Icon(
                            imageVector = if (content.tipo == "pelicula") Icons.Default.Movie else Icons.Default.Tv,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }

            // Botón de eliminar
            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

