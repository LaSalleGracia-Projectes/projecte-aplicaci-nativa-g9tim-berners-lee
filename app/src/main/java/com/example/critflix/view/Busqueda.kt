package com.example.critflix.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.critflix.Routes
import com.example.critflix.model.PelisPopulares
import com.example.critflix.model.SeriesPopulares
import com.example.critflix.viewmodel.APIViewModel
import com.example.critflix.viewmodel.SeriesViewModel

// Funcion principal, divide las partes de la view
@Composable
fun Busqueda(navController: NavHostController, apiViewModel: APIViewModel, seriesViewModel: SeriesViewModel) {
    val showLoading: Boolean by apiViewModel.loading.observeAsState(true)
    val peliculas: List<PelisPopulares> by apiViewModel.pelis.observeAsState(emptyList())
    val series: List<SeriesPopulares> by seriesViewModel.series.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        apiViewModel.getPelis(totalMoviesNeeded = 3000)
        seriesViewModel.getSeries(totalSeriesNeeded = 3000)
    }

    Scaffold(
        topBar = { TopBarBusqueda(navController) }
    ) { innerPadding ->
        if (showLoading) {
            LoadingIndicator()
        } else {
            ContenidoPrincipal(
                paddingValues = innerPadding,
                navController = navController,
                peliculas = peliculas,
                series = series,
                apiViewModel = apiViewModel,
                seriesViewModel = seriesViewModel
            )
        }
    }
}

// Indicador de carga para las apis
@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

// Parte superior de la view
@Composable
fun TopBarBusqueda(navController: NavHostController) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigate(Routes.Home.route) }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver"
                )
            }

            Text(
                text = "Búsqueda",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.width(24.dp))
        }
    }
}

// Un poco de la logica (hay que pasarlo al viewmodel correspondiente)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContenidoPrincipal(
    paddingValues: PaddingValues,
    navController: NavHostController,
    peliculas: List<PelisPopulares>,
    series: List<SeriesPopulares>,
    apiViewModel: APIViewModel,
    seriesViewModel: SeriesViewModel
) {
    var busqueda by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    val filteredPeliculas = remember(busqueda, peliculas) {
        if (busqueda.isBlank()) {
            emptyList()
        } else {
            peliculas.filter {
                it.title.contains(busqueda, ignoreCase = true)
            }
        }
    }

    val filteredSeries = remember(busqueda, series) {
        if (busqueda.isBlank()) {
            emptyList()
        } else {
            series.filter {
                it.name.contains(busqueda, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        SearchBar(
            query = busqueda,
            onQueryChange = {
                busqueda = it
                isSearchActive = it.isNotBlank()
            },
            onClearQuery = {
                busqueda = ""
                isSearchActive = false
            }
        )

        if (isSearchActive) {
            SearchResults(
                query = busqueda,
                filteredPeliculas = filteredPeliculas,
                filteredSeries = filteredSeries
            )
        } else {
            DefaultContent(
                peliculas = peliculas,
                series = series
            )
        }
    }
}

// Muestra una cosa u otra dependiendo si se encuentra
@Composable
fun SearchResults(
    query: String,
    filteredPeliculas: List<PelisPopulares>,
    filteredSeries: List<SeriesPopulares>
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Resultados para \"$query\"",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        if (filteredPeliculas.isEmpty() && filteredSeries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No se encontraron resultados que coincidan con \"$query\"",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                if (filteredPeliculas.isNotEmpty()) {
                    item {
                        Text(
                            text = "PELÍCULAS",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    items(filteredPeliculas) { pelicula ->
                        MovieCard(pelicula = pelicula)
                    }
                }

                if (filteredSeries.isNotEmpty()) {
                    item {
                        Text(
                            text = "SERIES",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    items(filteredSeries) { serie ->
                        SerieCard(serie = serie)
                    }
                }
            }
        }
    }
}

// Contenido normal de la vista
@Composable
fun DefaultContent(
    peliculas: List<PelisPopulares>,
    series: List<SeriesPopulares>
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {

        item {
            SectionHeader("PELÍCULAS TENDENCIA")
        }
        items(peliculas.take(3)) { pelicula ->
            MovieCard(pelicula = pelicula)
        }

        item {
            SectionHeader("SERIES TENDENCIA")
        }
        items(series.take(3)) { serie ->
            SerieCard(serie = serie)
        }

        item {
            SectionHeader("PARA TI")
        }
        items(peliculas.drop(3).take(2)) { pelicula ->
            MovieCard(pelicula = pelicula)
        }
        items(series.drop(3).take(2)) { serie ->
            SerieCard(serie = serie)
        }

        item {
            SectionHeader("TODAS LAS PELÍCULAS")
        }
        items(peliculas.take(10)) { pelicula ->
            MovieCard(pelicula = pelicula)
        }

        item {
            SectionHeader("TODAS LAS SERIES")
        }
        items(series.take(10)) { serie ->
            SerieCard(serie = serie)
        }
    }
}

// Barra de busqueda + filtro
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Busqueda"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Busca tus series o películas")
                }
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1f),
            singleLine = true,
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = onClearQuery) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Limpiar búsqueda"
                        )
                    }
                }
            }
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(onClick = { /* FILTRO */ }) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Filtrar"
            )
        }
    }
}

// Divisor de secciones
@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

// Elemento pelicula
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MovieCard(pelicula: PelisPopulares) {
    val baseImageUrl = "https://image.tmdb.org/t/p/w185"
    val posterUrl = baseImageUrl + pelicula.poster_path

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
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
            // Poster pelis
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(100.dp, 150.dp)
            ) {
                GlideImage(
                    model = posterUrl,
                    contentDescription = "Movie Poster",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Detalles pelis
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = pelicula.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Popularidad: ${pelicula.popularity}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Rating pelis
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "★ ${pelicula.vote_average}/10",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (pelicula.vote_average >= 7)
                            Color(0xFF4CAF50) else Color(0xFFFFA000)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Mostrar que es peli
                SuggestionChip(
                    onClick = { /* Click action */ },
                    label = { Text("Película") },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        labelColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.height(24.dp)
                )
            }
        }
    }
}

// Elemento serie
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SerieCard(serie: SeriesPopulares) {
    val baseImageUrl = "https://image.tmdb.org/t/p/w185"
    val posterUrl = baseImageUrl + serie.poster_path

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
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
            // Poster serie
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(100.dp, 150.dp)
            ) {
                GlideImage(
                    model = posterUrl,
                    contentDescription = "Series Poster",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Detalles series
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = serie.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Popularidad: ${serie.popularity}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Rating Series
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "★ ${serie.vote_average}/10",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (serie.vote_average >= 7)
                            Color(0xFF4CAF50) else Color(0xFFFFA000)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Marcar como serie
                SuggestionChip(
                    onClick = { /*  */ },
                    label = { Text("Serie") },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                        labelColor = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier.height(24.dp)
                )
            }
        }
    }
}