package com.example.critflix.view.compact

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.critflix.R
import com.example.critflix.nav.Routes
import com.example.critflix.model.Genre
import com.example.critflix.model.PelisPopulares
import com.example.critflix.model.SeriesPopulares
import com.example.critflix.model.UserSessionManager
import com.example.critflix.viewmodel.APIViewModel
import com.example.critflix.viewmodel.GenresViewModel
import com.example.critflix.viewmodel.ListViewModel
import com.example.critflix.viewmodel.NotificacionesViewModel
import com.example.critflix.viewmodel.SeriesViewModel

@Composable
fun HomeScreen(
    navController: NavHostController,
    apiViewModel: APIViewModel,
    seriesViewModel: SeriesViewModel,
    genresViewModel: GenresViewModel,
    listViewModel: ListViewModel,
    notificacionesViewModel: NotificacionesViewModel
) {
    val showLoading: Boolean by apiViewModel.loading.observeAsState(true)
    val peliculas: List<PelisPopulares> by apiViewModel.pelis.observeAsState(emptyList())
    val series: List<SeriesPopulares> by seriesViewModel.series.observeAsState(emptyList())
    val generos: List<Genre> by genresViewModel.genres.observeAsState(emptyList())
    val genreMap: Map<Int, String> by genresViewModel.genreMap.observeAsState(emptyMap())
    val context = LocalContext.current
    val userSessionManager = remember { UserSessionManager(context) }
    val token = userSessionManager.getToken() ?: ""
    val userId = userSessionManager.getUserId()
    var selectedFilter by remember { mutableStateOf("Todos") }

    LaunchedEffect(Unit) {
        apiViewModel.getPelis(totalMoviesNeeded = 50)
        seriesViewModel.getSeries(totalSeriesNeeded = 50)
        genresViewModel.loadGenres()
    }

    LaunchedEffect(userId, token) {
        if (userId > 0 && token.isNotEmpty()) {
            notificacionesViewModel.getUserNotificaciones(userId, token)
        }
    }

    LaunchedEffect(userId) {
        if (userId > 0) {
            listViewModel.loadUserLists(userId, token)
        }
    }

    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = { BottomNavigationBar(navController, notificacionesViewModel) }
    ) { innerPadding ->
        if (showLoading || generos.isEmpty()) {
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
        } else {
            HomeContent(
                innerPadding = innerPadding,
                peliculas = peliculas,
                series = series,
                generos = generos,
                genreMap = genreMap,
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it },
                navController = navController
            )
        }
    }
}

@Composable
fun TopBar(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Black)
            .height(60.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_critflix),
            contentDescription = "Logo",
            modifier = Modifier.size(50.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Anuncios",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { navController.navigate(Routes.Anuncios.route) },
                tint = Color.White
            )

            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Búsqueda",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { navController.navigate(Routes.Busqueda.route) },
                tint = Color.White
            )
        }
    }
}

@Composable
fun HomeContent(
    innerPadding: PaddingValues,
    peliculas: List<PelisPopulares>,
    series: List<SeriesPopulares>,
    generos: List<Genre>,
    genreMap: Map<Int, String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    navController: NavHostController
) {
    val contenidoFiltrado = when (selectedFilter) {
        "Películas" -> peliculas.map { ContentItem.Movie(it) }
        "Series" -> series.map { ContentItem.Series(it) }
        else -> {
            val combinedList = mutableListOf<ContentItem>()
            peliculas.forEach { combinedList.add(ContentItem.Movie(it)) }
            series.forEach { combinedList.add(ContentItem.Series(it)) }
            combinedList.sortedByDescending {
                when (it) {
                    is ContentItem.Movie -> it.pelicula.popularity
                    is ContentItem.Series -> it.serie.popularity
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black),
        contentPadding = PaddingValues(
            top = innerPadding.calculateTopPadding(),
            bottom = innerPadding.calculateBottomPadding() + 16.dp
        )
    ) {
        item {
            BotonesFiltro(selectedFilter, onFilterSelected)
        }

        item {
            if (contenidoFiltrado.isNotEmpty()) {
                when (val contenido = contenidoFiltrado.first()) {
                    is ContentItem.Movie -> PeliPopular(contenido.pelicula, navController)
                    is ContentItem.Series -> SeriePopular(contenido.serie, navController)
                }
            }
        }

        items(generos) { genero ->
            val contenidoPorGenero = contenidoFiltrado.filter { contenido ->
                when (contenido) {
                    is ContentItem.Movie -> contenido.pelicula.genre_ids.contains(genero.id)
                    is ContentItem.Series -> contenido.serie.genre_ids.contains(genero.id)
                }
            }

            if (contenidoPorGenero.isNotEmpty()) {
                GeneroSeccion(
                    genero = genero.name,
                    contenido = contenidoPorGenero,
                    navController = navController,
                    cantidadContenido = contenidoPorGenero.size
                )
            }
        }
    }
}

@Composable
fun BotonesFiltro(selectedFilter: String, onFilterSelected: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Black)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BotonFiltro("Todos", selected = selectedFilter == "Todos") {
            onFilterSelected("Todos")
        }
        BotonFiltro("Series", selected = selectedFilter == "Series") {
            onFilterSelected("Series")
        }
        BotonFiltro("Películas", selected = selectedFilter == "Películas") {
            onFilterSelected("Películas")
        }
    }
}

@Composable
fun BotonFiltro(text: String, selected: Boolean, onClick: () -> Unit = {}) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.Black,
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) Color.Green else Color.White
        ),
        modifier = Modifier
            .wrapContentWidth()
            .height(32.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = if (selected) Color.Green else Color.White
            )
        }
    }
}

sealed class ContentItem {
    data class Movie(val pelicula: PelisPopulares) : ContentItem()
    data class Series(val serie: SeriesPopulares) : ContentItem()
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PeliPopular(pelicula: PelisPopulares, navController: NavHostController) {
    val baseImageUrl = "https://image.tmdb.org/t/p/original"
    val posterUrl = baseImageUrl + pelicula.poster_path

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(vertical = 8.dp)
            .clickable { navController.navigate(Routes.InfoPelis.createRoute(pelicula.id)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { navController.navigate(Routes.InfoPelis.createRoute(pelicula.id)) }
        ) {
            GlideImage(
                model = posterUrl,
                contentDescription = pelicula.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f/3f)
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SeriePopular(serie: SeriesPopulares, navController: NavHostController) {
    val baseImageUrl = "https://image.tmdb.org/t/p/original"
    val posterUrl = baseImageUrl + serie.poster_path

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { navController.navigate(Routes.InfoSeries.createRoute(serie.id)) }
        ) {
            GlideImage(
                model = posterUrl,
                contentDescription = serie.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f/3f)
            )
        }
    }
}

@Composable
fun GeneroSeccion(
    genero: String,
    contenido: List<ContentItem>,
    navController: NavHostController,
    cantidadContenido: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = genero,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "$cantidadContenido títulos",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        if (contenido.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(contenido) { item ->
                    when (item) {
                        is ContentItem.Movie -> PeliCarrusel(item.pelicula, navController)
                        is ContentItem.Series -> SerieCarrusel(item.serie, navController)
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay contenido disponible para este género",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PeliCarrusel(pelicula: PelisPopulares, navController: NavHostController) {
    val baseImageUrl = "https://image.tmdb.org/t/p/w185"
    val posterUrl = baseImageUrl + pelicula.poster_path

    Column(
        modifier = Modifier
            .width(120.dp)
            .clickable { navController.navigate(Routes.InfoPelis.createRoute(pelicula.id)) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .width(120.dp)
                .height(180.dp)
        ) {
            GlideImage(
                model = posterUrl,
                contentDescription = pelicula.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Text(
            text = pelicula.title,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp),
            color = Color.White
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SerieCarrusel(serie: SeriesPopulares, navController: NavHostController) {
    val baseImageUrl = "https://image.tmdb.org/t/p/w185"
    val posterUrl = baseImageUrl + serie.poster_path

    Column(
        modifier = Modifier
            .width(120.dp)
            .clickable { navController.navigate(Routes.InfoSeries.createRoute(serie.id)) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .width(120.dp)
                .height(180.dp)
        ) {
            GlideImage(
                model = posterUrl,
                contentDescription = serie.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Text(
            text = serie.name,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp),
            color = Color.White
        )
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    notificacionesViewModel: NotificacionesViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Observamos el estado de las notificaciones no leídas
    val notificaciones by notificacionesViewModel.notificaciones.observeAsState(initial = emptyList())
    val hasUnreadNotifications = notificaciones.any { !it.leido }

    NavigationBar {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = if (currentDestination?.hierarchy?.any { it.route == Routes.Home.route } == true) {
                        Color(0xFF1DB954)
                    } else {
                        Color.White
                    }
                )
            },
            label = { Text("Inicio", color = Color.White) },
            selected = currentDestination?.hierarchy?.any { it.route == Routes.Home.route } == true,
            onClick = { navController.navigate(Routes.Home.route) }
        )

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "Lists",
                    tint = if (currentDestination?.hierarchy?.any { it.route == Routes.Listas.route } == true) {
                        Color(0xFF1DB954)
                    } else {
                        Color.White
                    }
                )
            },
            label = { Text("Listas", color = Color.White) },
            selected = currentDestination?.hierarchy?.any { it.route == Routes.Listas.route } == true,
            onClick = { navController.navigate(Routes.Listas.route) }
        )

        NavigationBarItem(
            icon = {
                Box {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = if (currentDestination?.hierarchy?.any { it.route == Routes.Notificaciones.route } == true) {
                            Color(0xFF1DB954)
                        } else {
                            Color.White
                        }
                    )
                    if (hasUnreadNotifications && currentDestination?.hierarchy?.any { it.route == Routes.Notificaciones.route } != true) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = 2.dp, y = (-2).dp)
                                .clip(CircleShape)
                                .background(Color.Red)
                        )
                    }
                }
            },
            label = { Text("Notificaciones", color = Color.White) },
            selected = currentDestination?.hierarchy?.any { it.route == Routes.Notificaciones.route } == true,
            onClick = { navController.navigate(Routes.Notificaciones.route) }
        )

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    tint = if (currentDestination?.hierarchy?.any { it.route == Routes.Perfil.route } == true) {
                        Color(0xFF1DB954)
                    } else {
                        Color.White
                    }
                )
            },
            label = { Text("Perfil", color = Color.White) },
            selected = currentDestination?.hierarchy?.any { it.route == Routes.Perfil.route } == true,
            onClick = { navController.navigate(Routes.Perfil.route) }
        )
    }
}