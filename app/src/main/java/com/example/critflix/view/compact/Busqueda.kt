package com.example.critflix.view.compact

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.critflix.nav.Routes
import com.example.critflix.model.PelisPopulares
import com.example.critflix.model.SeriesPopulares
import com.example.critflix.viewmodel.APIViewModel
import com.example.critflix.viewmodel.BusquedaViewModel
import com.example.critflix.viewmodel.ContentType
import com.example.critflix.viewmodel.SeriesViewModel
import com.example.critflix.viewmodel.SortCriteria
import com.example.critflix.viewmodel.SortDirection


// Funcion principal, divide las partes de la view
@Composable
fun Busqueda(
    navController: NavHostController,
    apiViewModel: APIViewModel,
    seriesViewModel: SeriesViewModel,
    busquedaViewModel: BusquedaViewModel
) {
    val showLoading: Boolean by apiViewModel.loading.observeAsState(true)
    val peliculas: List<PelisPopulares> by apiViewModel.pelis.observeAsState(emptyList())
    val series: List<SeriesPopulares> by seriesViewModel.series.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        apiViewModel.getPelis(totalMoviesNeeded = 2000)
        seriesViewModel.getSeries(totalSeriesNeeded = 2000)
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
                seriesViewModel = seriesViewModel,
                busquedaViewModel = busquedaViewModel
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

// Contenido principal de la view que ordena la view
@Composable
fun ContenidoPrincipal(
    paddingValues: PaddingValues,
    navController: NavHostController,
    peliculas: List<PelisPopulares>,
    series: List<SeriesPopulares>,
    apiViewModel: APIViewModel,
    seriesViewModel: SeriesViewModel,
    busquedaViewModel: BusquedaViewModel
) {
    // Barra de busqueda
    val busqueda: String by busquedaViewModel.searchQuery.observeAsState("")
    val isSearchActive: Boolean by busquedaViewModel.isSearchActive.observeAsState(false)
    val filteredPeliculas: List<PelisPopulares> by busquedaViewModel.filteredPeliculas.observeAsState(emptyList())
    val filteredSeries: List<SeriesPopulares> by busquedaViewModel.filteredSeries.observeAsState(emptyList())
    // Filtros
    val showFilterDialog: Boolean by busquedaViewModel.showFilterDialog.observeAsState(false)
    val contentType: ContentType by busquedaViewModel.contentType.observeAsState(ContentType.ALL)
    val sortCriteria: SortCriteria by busquedaViewModel.sortCriteria.observeAsState(SortCriteria.POPULARITY)
    val sortDirection: SortDirection by busquedaViewModel.sortDirection.observeAsState(SortDirection.DESCENDING)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        SearchBar(
            query = busqueda,
            onQueryChange = { newQuery ->
                busquedaViewModel.updateSearchQuery(newQuery, peliculas, series)
            },
            onClearQuery = {
                busquedaViewModel.clearSearch()
            },
            onFilterClick = {
                busquedaViewModel.toggleFilterDialog()
            }
        )

        // Dialogo de filtros
        FilterDialog(
            show = showFilterDialog,
            contentType = contentType,
            sortCriteria = sortCriteria,
            sortDirection = sortDirection,
            onContentTypeChanged = { busquedaViewModel.updateContentType(it) },
            onSortCriteriaChanged = { busquedaViewModel.updateSortCriteria(it) },
            onSortDirectionChanged = { busquedaViewModel.updateSortDirection(it) },
            onDismiss = { busquedaViewModel.toggleFilterDialog() },
            onApply = {
                busquedaViewModel.applyFiltersAndSort(busqueda, peliculas, series)
            }
        )

        if (isSearchActive) {
            SearchResults(
                query = busqueda,
                filteredPeliculas = filteredPeliculas,
                filteredSeries = filteredSeries,
                navController = navController,
                contentType = contentType
            )
        } else {
            DefaultContent(
                peliculas = peliculas,
                series = series,
                navController = navController
            )
        }
    }
}

// Lo que se muestra dependiendo de la busqueda
@Composable
fun SearchResults(
    query: String,
    filteredPeliculas: List<PelisPopulares>,
    filteredSeries: List<SeriesPopulares>,
    navController: NavHostController,
    contentType: ContentType
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
                if (filteredPeliculas.isNotEmpty() && contentType != ContentType.SERIES) {
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
                        MovieCard(pelicula = pelicula, navController = navController)
                    }
                }

                if (filteredSeries.isNotEmpty() && contentType != ContentType.MOVIES) {
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
                        SerieCard(serie = serie, navController = navController)
                    }
                }
            }
        }
    }
}

// Contenido default de la vista
@Composable
fun DefaultContent(
    peliculas: List<PelisPopulares>,
    series: List<SeriesPopulares>,
    navController: NavHostController
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {

        item {
            SectionHeader("PELÍCULAS TENDENCIA")
        }
        items(peliculas.take(3)) { pelicula ->
            MovieCard(pelicula = pelicula, navController = navController)
        }

        item {
            SectionHeader("SERIES TENDENCIA")
        }
        items(series.take(3)) { serie ->
            SerieCard(serie = serie, navController = navController)
        }

        item {
            SectionHeader("PARA TI")
        }
        items(peliculas.drop(3).take(2)) { pelicula ->
            MovieCard(pelicula = pelicula, navController = navController)
        }
        items(series.drop(3).take(2)) { serie ->
            SerieCard(serie = serie, navController = navController)
        }

        item {
            SectionHeader("TODAS LAS PELÍCULAS")
        }
        items(peliculas.take(10)) { pelicula ->
            MovieCard(pelicula = pelicula, navController = navController)
        }

        item {
            SectionHeader("TODAS LAS SERIES")
        }
        items(series.take(10)) { serie ->
            SerieCard(serie = serie, navController = navController)
        }
    }
}

// Actualización de la barra de búsqueda con el botón de filtros
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onFilterClick: () -> Unit
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

        IconButton(
            onClick = onFilterClick,
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Filtrar",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

// Diálogo de filtros
@Composable
fun FilterDialog(
    show: Boolean,
    contentType: ContentType,
    sortCriteria: SortCriteria,
    sortDirection: SortDirection,
    onContentTypeChanged: (ContentType) -> Unit,
    onSortCriteriaChanged: (SortCriteria) -> Unit,
    onSortDirectionChanged: (SortDirection) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit
) {
    if (show) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Título
                    Text(
                        text = "Busqueda avanzada",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Sección: Tipo de contenido
                    Text(
                        text = "TIPO DE CONTENIDO",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = contentType == ContentType.ALL,
                            onClick = { onContentTypeChanged(ContentType.ALL) },
                            label = { Text("Todos", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = contentType == ContentType.MOVIES,
                            onClick = { onContentTypeChanged(ContentType.MOVIES) },
                            label = { Text("Pelis", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = contentType == ContentType.SERIES,
                            onClick = { onContentTypeChanged(ContentType.SERIES) },
                            label = { Text("Series", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Sección: Criterio de ordenación
                    Text(
                        text = "ORDENAR POR",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Column(modifier = Modifier.fillMaxWidth()) {
                        RadioButtonWithText(
                            text = "Alfabético",
                            selected = sortCriteria == SortCriteria.ALPHABETICAL,
                            onClick = { onSortCriteriaChanged(SortCriteria.ALPHABETICAL) }
                        )
                        RadioButtonWithText(
                            text = "Fecha de lanzamiento",
                            selected = sortCriteria == SortCriteria.RELEASE_DATE,
                            onClick = { onSortCriteriaChanged(SortCriteria.RELEASE_DATE) }
                        )
                        RadioButtonWithText(
                            text = "Popularidad",
                            selected = sortCriteria == SortCriteria.POPULARITY,
                            onClick = { onSortCriteriaChanged(SortCriteria.POPULARITY) }
                        )
                        RadioButtonWithText(
                            text = "Puntuación",
                            selected = sortCriteria == SortCriteria.RATING,
                            onClick = { onSortCriteriaChanged(SortCriteria.RATING) }
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Sección: Orden
                    Text(
                        text = "ORDEN",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = sortDirection == SortDirection.ASCENDING,
                            onClick = { onSortDirectionChanged(SortDirection.ASCENDING) },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ArrowUpward, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Ascendente", fontSize = 12.sp)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = sortDirection == SortDirection.DESCENDING,
                            onClick = { onSortDirectionChanged(SortDirection.DESCENDING) },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Descendente", fontSize = 12.sp)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botones de Cancelar y Aplicar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancelar")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            onApply()
                            onDismiss()
                        }) {
                            Text("Aplicar")
                        }
                    }
                }
            }
        }
    }
}

// Componente para radio buttons con texto
@Composable
fun RadioButtonWithText(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)
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
fun MovieCard(pelicula: PelisPopulares, navController: NavHostController) {
    val baseImageUrl = "https://image.tmdb.org/t/p/w185"
    val posterUrl = baseImageUrl + pelicula.poster_path

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable{ navController.navigate(Routes.InfoPelis.createRoute(pelicula.id)) },
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
fun SerieCard(serie: SeriesPopulares, navController: NavHostController) {
    val baseImageUrl = "https://image.tmdb.org/t/p/w185"
    val posterUrl = baseImageUrl + serie.poster_path

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable{ navController.navigate(Routes.InfoSeries.createRoute(serie.id)) }
        ,
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