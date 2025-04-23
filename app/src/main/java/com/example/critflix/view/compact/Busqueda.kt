package com.example.critflix.view.compact

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.LiveData
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.critflix.model.Genre
import com.example.critflix.nav.Routes
import com.example.critflix.model.PelisPopulares
import com.example.critflix.model.SeriesPopulares
import com.example.critflix.model.UserSessionManager
import com.example.critflix.viewmodel.APIViewModel
import com.example.critflix.viewmodel.BusquedaViewModel
import com.example.critflix.viewmodel.ContenidoListaViewModel
import com.example.critflix.viewmodel.ContentType
import com.example.critflix.viewmodel.GenresViewModel
import com.example.critflix.viewmodel.SeriesViewModel
import com.example.critflix.viewmodel.SortCriteria
import com.example.critflix.viewmodel.SortDirection


// Funcion principal, divide las partes de la view
@Composable
fun Busqueda(
    navController: NavHostController,
    apiViewModel: APIViewModel,
    seriesViewModel: SeriesViewModel,
    busquedaViewModel: BusquedaViewModel,
    genresViewModel: GenresViewModel,
    contenidoListaViewModel: ContenidoListaViewModel,
    listaId: String? = null
) {
    val showLoading: Boolean by apiViewModel.loading.observeAsState(true)
    val peliculas: List<PelisPopulares> by apiViewModel.pelis.observeAsState(emptyList())
    val series: List<SeriesPopulares> by seriesViewModel.series.observeAsState(emptyList())
    val genresLoading: Boolean by genresViewModel.loading.observeAsState(true)

    val isAddToListMode = listaId != null

    val context = LocalContext.current
    val userSessionManager = remember { UserSessionManager(context) }
    val token = userSessionManager.getToken() ?: ""

    LaunchedEffect(Unit) {
        apiViewModel.getPelis(totalMoviesNeeded = 500)
        seriesViewModel.getSeries(totalSeriesNeeded = 500)
        genresViewModel.loadGenres()

        if (isAddToListMode && listaId != null) {
            contenidoListaViewModel.loadListContent(listaId, token)
        }
    }

    Scaffold(
        topBar = {
            TopBarBusqueda(
                navController = navController,
                isAddToListMode = isAddToListMode,
                listaName = if (isAddToListMode) "Añadir a lista" else "Búsqueda"
            )
        }
    ) { innerPadding ->
        if (showLoading || genresLoading) {
            LoadingIndicator()
        } else {
            ContenidoPrincipal(
                paddingValues = innerPadding,
                navController = navController,
                viewModel = busquedaViewModel,
                peliculasLiveData = apiViewModel.pelis,
                seriesLiveData = seriesViewModel.series,
                genresLiveData = genresViewModel.genres,
                isAddToListMode = isAddToListMode,
                listaId = listaId,
                contenidoListaViewModel = contenidoListaViewModel,
                token = token
            )
        }
    }
}

// Indicador de carga para las apis
@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.secondary
        )

    }
}

// Parte superior de la view
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarBusqueda(
    navController: NavHostController,
    isAddToListMode: Boolean = false,
    listaName: String = "Búsqueda"
) {
    TopAppBar(
        title = {
            Text(
                text = listaName,
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

// Contenido principal de la view que ordena la view
@Composable
fun ContenidoPrincipal(
    paddingValues: PaddingValues,
    navController: NavHostController,
    viewModel: BusquedaViewModel,
    peliculasLiveData: LiveData<List<PelisPopulares>>,
    seriesLiveData: LiveData<List<SeriesPopulares>>,
    genresLiveData: LiveData<List<Genre>>,
    isAddToListMode: Boolean = false,
    listaId: String? = null,
    contenidoListaViewModel: ContenidoListaViewModel? = null,
    token: String = ""
) {
    val peliculas by peliculasLiveData.observeAsState(initial = emptyList())
    val series by seriesLiveData.observeAsState(initial = emptyList())
    val genres by genresLiveData.observeAsState(initial = emptyList())

    val query by viewModel.searchQuery.observeAsState("")
    val isSearchActive by viewModel.isSearchActive.observeAsState(false)
    val filteredPeliculas by viewModel.filteredPeliculas.observeAsState(emptyList())
    val filteredSeries by viewModel.filteredSeries.observeAsState(emptyList())

    val contentType by viewModel.contentType.observeAsState(ContentType.ALL)
    val sortCriteria by viewModel.sortCriteria.observeAsState(SortCriteria.POPULARITY)
    val sortDirection by viewModel.sortDirection.observeAsState(SortDirection.DESCENDING)
    val selectedGenreIds by viewModel.selectedGenreIds.observeAsState(emptySet())
    val showFilterDialog by viewModel.showFilterDialog.observeAsState(false)

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .padding(paddingValues)
    ) {
        SearchBar(
            query = query,
            onQueryChange = { newQuery ->
                viewModel.updateSearchQuery(newQuery, peliculas, series)
            },
            onClearQuery = {
                viewModel.clearSearch()
            },
            onFilterClick = {
                viewModel.toggleFilterDialog()
            }
        )

        FilterDialog(
            show = showFilterDialog,
            contentType = contentType,
            sortCriteria = sortCriteria,
            sortDirection = sortDirection,
            availableGenres = genres,
            selectedGenreIds = selectedGenreIds,
            onContentTypeChanged = { viewModel.updateContentType(it) },
            onSortCriteriaChanged = { viewModel.updateSortCriteria(it) },
            onSortDirectionChanged = { viewModel.updateSortDirection(it) },
            onGenreToggled = { viewModel.toggleGenreSelection(it) },
            onClearGenres = { viewModel.clearGenreSelection() },
            onDismiss = { viewModel.toggleFilterDialog() },
            onApply = {
                viewModel.applyFiltersAndSort(query, peliculas, series)
            }
        )

        if (isSearchActive) {
            SearchResults(
                query = query,
                filteredPeliculas = filteredPeliculas,
                filteredSeries = filteredSeries,
                navController = navController,
                contentType = contentType,
                isAddToListMode = isAddToListMode,
                listaId = listaId,
                contenidoListaViewModel = contenidoListaViewModel,
                token = token,
                context = context
            )
        } else {
            DefaultContent(
                peliculas = peliculas,
                series = series,
                navController = navController,
                isAddToListMode = isAddToListMode,
                listaId = listaId,
                contenidoListaViewModel = contenidoListaViewModel,
                token = token,
                context = context
            )
        }
    }
}

// Resultados de la busqueda
@Composable
fun SearchResults(
    query: String,
    filteredPeliculas: List<PelisPopulares>,
    filteredSeries: List<SeriesPopulares>,
    navController: NavHostController,
    contentType: ContentType,
    isAddToListMode: Boolean = false,
    listaId: String? = null,
    contenidoListaViewModel: ContenidoListaViewModel? = null,
    token: String = "",
    context: Context
) {
    val existingContent = if (contenidoListaViewModel != null) {
        contenidoListaViewModel.contentItems.observeAsState(emptyList()).value
    } else {
        emptyList()
    }
    val existingMovieIds = existingContent.filter { it.tipo == "pelicula" }.map { it.tmdb_id }.toSet()
    val existingSeriesIds = existingContent.filter { it.tipo == "serie" }.map { it.tmdb_id }.toSet()

    Column(
        modifier = Modifier.fillMaxWidth().background(color = Color.Black)
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
                        MovieCard(
                            pelicula = pelicula,
                            navController = navController,
                            isAddToListMode = isAddToListMode,
                            existingContentIds = existingMovieIds,
                            onAddToList = {
                                if (listaId != null && contenidoListaViewModel != null) {
                                    contenidoListaViewModel.addContentToList(
                                        listaId = listaId,
                                        tmdbId = pelicula.id,
                                        tipo = "pelicula",
                                        token = token
                                    )
                                    Toast.makeText(context, "Película añadida a la lista", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
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
                        SerieCard(
                            serie = serie,
                            navController = navController,
                            isAddToListMode = isAddToListMode,
                            existingContentIds = existingSeriesIds,
                            onAddToList = {
                                if (listaId != null && contenidoListaViewModel != null) {
                                    contenidoListaViewModel.addContentToList(
                                        listaId = listaId,
                                        tmdbId = serie.id,
                                        tipo = "serie",
                                        token = token
                                    )
                                    Toast.makeText(context, "Serie añadida a la lista", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DefaultContent(
    peliculas: List<PelisPopulares>,
    series: List<SeriesPopulares>,
    navController: NavHostController,
    isAddToListMode: Boolean = false,
    listaId: String? = null,
    contenidoListaViewModel: ContenidoListaViewModel? = null,
    token: String = "",
    context: Context
) {
    val existingContent = if (contenidoListaViewModel != null) {
        contenidoListaViewModel.contentItems.observeAsState(emptyList()).value
    } else {
        emptyList()
    }

    val existingMovieIds = existingContent.filter { it.tipo == "pelicula" }.map { it.tmdb_id }.toSet()
    val existingSeriesIds = existingContent.filter { it.tipo == "serie" }.map { it.tmdb_id }.toSet()

    LazyColumn(
        modifier = Modifier.fillMaxWidth().background(color = Color.Black),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            SectionHeader("PELÍCULAS TENDENCIA")
        }
        items(peliculas.take(3)) { pelicula ->
            MovieCard(
                pelicula = pelicula,
                navController = navController,
                isAddToListMode = isAddToListMode,
                existingContentIds = existingMovieIds,
                onAddToList = {
                    if (listaId != null && contenidoListaViewModel != null) {
                        contenidoListaViewModel.addContentToList(
                            listaId = listaId,
                            tmdbId = pelicula.id,
                            tipo = "pelicula",
                            token = token
                        )
                        Toast.makeText(context, "Película añadida a la lista", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        item {
            SectionHeader("SERIES TENDENCIA")
        }
        items(series.take(3)) { serie ->
            SerieCard(
                serie = serie,
                navController = navController,
                isAddToListMode = isAddToListMode,
                existingContentIds = existingSeriesIds,
                onAddToList = {
                    if (listaId != null && contenidoListaViewModel != null) {
                        contenidoListaViewModel.addContentToList(
                            listaId = listaId,
                            tmdbId = serie.id,
                            tipo = "serie",
                            token = token
                        )
                        Toast.makeText(context, "Serie añadida a la lista", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        item {
            SectionHeader("PARA TI")
        }
        items(peliculas.drop(3).take(2)) { pelicula ->
            MovieCard(
                pelicula = pelicula,
                navController = navController,
                isAddToListMode = isAddToListMode,
                existingContentIds = existingMovieIds,
                onAddToList = {
                    if (listaId != null && contenidoListaViewModel != null) {
                        contenidoListaViewModel.addContentToList(
                            listaId = listaId,
                            tmdbId = pelicula.id,
                            tipo = "pelicula",
                            token = token
                        )
                        Toast.makeText(context, "Película añadida a la lista", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
        items(series.drop(3).take(2)) { serie ->
            SerieCard(
                serie = serie,
                navController = navController,
                isAddToListMode = isAddToListMode,
                existingContentIds = existingSeriesIds,
                onAddToList = {
                    if (listaId != null && contenidoListaViewModel != null) {
                        contenidoListaViewModel.addContentToList(
                            listaId = listaId,
                            tmdbId = serie.id,
                            tipo = "serie",
                            token = token
                        )
                        Toast.makeText(context, "Serie añadida a la lista", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}

// Barra de busqueda
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
            .background(color = Color.Black)
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
    availableGenres: List<Genre>,
    selectedGenreIds: Set<Int>,
    onContentTypeChanged: (ContentType) -> Unit,
    onSortCriteriaChanged: (SortCriteria) -> Unit,
    onSortDirectionChanged: (SortDirection) -> Unit,
    onGenreToggled: (Int) -> Unit,
    onClearGenres: () -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit
) {
    if (show) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.Black,
                tonalElevation = 6.dp,
                border = BorderStroke(2.dp, Color.Green),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 500.dp)
                        .padding(16.dp)
                        .background(color = Color.Black)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Título
                    Text(
                        text = "Busqueda avanzada",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    // Tipo de contenido
                    Text(
                        text = "TIPO DE CONTENIDO",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(color = Color.Black),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = contentType == ContentType.ALL,
                            onClick = { onContentTypeChanged(ContentType.ALL) },
                            label = {
                                Text(
                                    "Todos",
                                    fontSize = 12.sp,
                                    color = if (contentType == ContentType.ALL) Color.White else Color.Gray
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color.Green.copy(alpha = 0.7f),
                            )
                        )

                        FilterChip(
                            selected = contentType == ContentType.MOVIES,
                            onClick = { onContentTypeChanged(ContentType.MOVIES) },
                            label = {
                                Text(
                                    "Pelis",
                                    fontSize = 12.sp,
                                    color = if (contentType == ContentType.MOVIES) Color.White else Color.Gray
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color.Green.copy(alpha = 0.7f),
                            )
                        )

                        FilterChip(
                            selected = contentType == ContentType.SERIES,
                            onClick = { onContentTypeChanged(ContentType.SERIES) },
                            label = {
                                Text(
                                    "Series",
                                    fontSize = 12.sp,
                                    color = if (contentType == ContentType.SERIES) Color.White else Color.Gray
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color.Green.copy(alpha = 0.7f),
                            )
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Géneros
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "GÉNEROS",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.secondary,
                        )

                        if (selectedGenreIds.isNotEmpty()) {
                            TextButton(
                                onClick = onClearGenres,
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = Color.Green
                                )
                            ) {
                                Text("Limpiar", fontSize = 12.sp)
                            }
                        }
                    }

                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        maxItemsInEachRow = 3
                    ) {
                        availableGenres.forEach { genre ->
                            val isSelected = selectedGenreIds.contains(genre.id)
                            FilterChip(
                                selected = isSelected,
                                onClick = { onGenreToggled(genre.id) },
                                label = {
                                    Text(
                                        genre.name,
                                        fontSize = 12.sp,
                                        color = if (isSelected) Color.White else Color.Gray
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color.Green.copy(alpha = 0.7f),
                                ),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Criterio de ordenación
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

                    // Orden
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
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color.Green.copy(alpha = 0.7f),
                            )
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
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color.Green.copy(alpha = 0.7f),
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botones de Cancelar y Aplicar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onDismiss,
                            border = BorderStroke(1.dp, Color.White),
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color.Red
                            ),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("Cancelar")
                        }

                        Button(
                            onClick = {
                                onApply()
                                onDismiss()
                            },
                            border = BorderStroke(1.dp, Color.White),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.Green
                            )
                        ) {
                            Text("Aplicar")
                        }
                    }
                }
            }
        }
    }
}

// Componente auxiliar para los chips de género en filas
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    maxItemsInEachRow: Int = Int.MAX_VALUE,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val rows = mutableListOf<MutableList<Placeable>>()
        val itemConstraints = constraints.copy(minWidth = 0)
        var currentRow = mutableListOf<Placeable>()
        var currentRowWidth = 0
        var itemsInCurrentRow = 0

        measurables.forEach { measurable ->
            val placeable = measurable.measure(itemConstraints)

            if (currentRowWidth + placeable.width > constraints.maxWidth || itemsInCurrentRow >= maxItemsInEachRow) {
                rows.add(currentRow)
                currentRow = mutableListOf()
                currentRowWidth = 0
                itemsInCurrentRow = 0
            }

            currentRow.add(placeable)
            currentRowWidth += placeable.width
            itemsInCurrentRow++
        }

        if (currentRow.isNotEmpty()) {
            rows.add(currentRow)
        }

        val height = rows.sumOf { row ->
            row.maxOfOrNull { it.height } ?: 0
        }

        layout(constraints.maxWidth, height) {
            var y = 0

            rows.forEach { row ->
                var x = 0
                val rowHeight = row.maxOfOrNull { it.height } ?: 0

                row.forEach { placeable ->
                    placeable.place(x, y)
                    x += placeable.width
                }

                y += rowHeight
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
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = Color.Green,
                unselectedColor = Color.Gray
            )
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (selected) Color.White else Color.Gray,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

// Divisor de secciones
@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}


// Tarjeta de pelicula
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MovieCard(
    pelicula: PelisPopulares,
    navController: NavHostController,
    isAddToListMode: Boolean = false,
    onAddToList: () -> Unit = {},
    existingContentIds: Set<Int> = emptySet()
) {
    val baseImageUrl = "https://image.tmdb.org/t/p/w185"
    val posterUrl = baseImageUrl + pelicula.poster_path
    val isInList = existingContentIds.contains(pelicula.id)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable {
                if (!isAddToListMode) {
                    navController.navigate(Routes.InfoPelis.createRoute(pelicula.id))
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
                    text = "Año: ${pelicula.release_date?.take(4) ?: "N/A"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Rating pelis
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val ratingColor = when {
                        pelicula.vote_average >= 7 -> Color(0xFF4CAF50)
                        pelicula.vote_average >= 5 -> Color(0xFFFFA000)
                        else -> Color(0xFFF44336)
                    }

                    Text(
                        text = "★ ${String.format("%.1f", pelicula.vote_average)}/10",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ratingColor
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
                    modifier = Modifier.height(24.dp),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }

            // Botón de añadir si estamos en modo de añadir a lista
            if (isAddToListMode) {
                IconButton(
                    onClick = {
                        if (!isInList) {
                            onAddToList()
                        }
                    },
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = if (isInList) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = if (isInList) "Ya en lista" else "Añadir a lista",
                        tint = if (isInList) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// Tarjeta de serie
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SerieCard(
    serie: SeriesPopulares,
    navController: NavHostController,
    isAddToListMode: Boolean = false,
    onAddToList: () -> Unit = {},
    existingContentIds: Set<Int> = emptySet()
) {
    val baseImageUrl = "https://image.tmdb.org/t/p/w185"
    val posterUrl = baseImageUrl + serie.poster_path
    val isInList = existingContentIds.contains(serie.id)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable {
                if (!isAddToListMode) {
                    navController.navigate(Routes.InfoSeries.createRoute(serie.id))
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
                    text = "Año: ${serie.first_air_date?.take(4) ?: "N/A"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Rating Series
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val ratingColor = when {
                        serie.vote_average >= 7 -> Color(0xFF4CAF50)
                        serie.vote_average >= 5 -> Color(0xFFFFA000)
                        else -> Color(0xFFF44336)
                    }

                    Text(
                        text = "★ ${String.format("%.1f", serie.vote_average)}/10",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ratingColor
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
                    modifier = Modifier.height(24.dp),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Tv,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }

            // Botón de añadir si estamos en modo de añadir a lista
            if (isAddToListMode) {
                IconButton(
                    onClick = {
                        if (!isInList) {
                            onAddToList()
                        }
                    },
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = if (isInList) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = if (isInList) "Ya en lista" else "Añadir a lista",
                        tint = if (isInList) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}