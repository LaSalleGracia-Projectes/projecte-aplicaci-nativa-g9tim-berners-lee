package com.example.critflix.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.critflix.R
import com.example.critflix.Routes
import com.example.critflix.model.Data
import com.example.critflix.model.PelisPopulares
import com.example.critflix.viewmodel.APIViewModel
import okio.utf8Size

@Composable
fun HomeScreen(navController: NavHostController, apiViewModel: APIViewModel) {
    val showLoading: Boolean by apiViewModel.loading.observeAsState(true)
    val peliculas: List<PelisPopulares> by apiViewModel.pelis.observeAsState(emptyList())
    val error: String? by apiViewModel.error.observeAsState()

    LaunchedEffect(Unit) {
        apiViewModel.getPelis(totalMoviesNeeded = 50)
    }

    Scaffold(
        topBar = {
            TopBar(navController)
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        if (showLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        } else if (error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = error ?: "Error desconocido",
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            HomeContent(
                innerPadding = innerPadding,
                peliculas = peliculas,
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
            .height(60.dp)
            //.background(brush = Brush.horizontalGradient(listOf(Color.Green, Color.Black)))
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo_critflix),
            contentDescription = "Logo",
            modifier = Modifier.size(50.dp)
        )

        // Iconos
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Anuncios",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { navController.navigate(Routes.Anuncios.route) }
            )

            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Búsqueda",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { navController.navigate(Routes.Busqueda.route) }
            )
        }
    }
}

@Composable
fun HomeContent(
    innerPadding: PaddingValues,
    peliculas: List<PelisPopulares>,
    navController: NavHostController
) {
    val generos = listOf("Acción", "Comedia", "Drama", "Terror", "Ciencia Ficción")
    val peliculasMasPopulares = peliculas.sortedByDescending { it.popularity }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = innerPadding.calculateTopPadding(),
            bottom = innerPadding.calculateBottomPadding() + 16.dp
        )
    ) {
        item {
            BotonesFiltro()
        }

        item {
            if (peliculasMasPopulares.isNotEmpty()) {
                PeliPopular(pelicula = peliculasMasPopulares.first(), navController = navController)
            }
        }

        items(generos) { genero ->
            GeneroSeccion(
                genero = genero,
                peliculas = peliculasMasPopulares,
                navController = navController
            )
        }
    }
}

@Composable
fun BotonesFiltro() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BotonFiltro("Series", selected = false)
        BotonFiltro("Películas", selected = false)
        BotonFiltro("Categorías", selected = true)
    }
}

@Composable
fun BotonFiltro(text: String, selected: Boolean) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (selected) Color.Gray.copy(alpha = 0.3f) else Color.LightGray.copy(alpha = 0.3f),
        modifier = Modifier
            .wrapContentWidth()
            .height(32.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = if (selected) Color.Black else Color.DarkGray
            )
            if (selected) {
                Icon(
                    painter = painterResource(id = R.drawable.desplegable),
                    contentDescription = "Seleccionado",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
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
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { navController.navigate(Routes.Info.createRoute(pelicula.id)) }
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

@Composable
fun GeneroSeccion(genero: String, peliculas: List<PelisPopulares>, navController: NavHostController) {
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
            // Titulo de la sección
            Text(
                text = genero,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Contador de películas
            Text(
                text = "${peliculas.take(50).size} películas",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        // Carrusel
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(
                items = peliculas.take(50),
                key = { pelicula -> pelicula.id }
            ) { pelicula ->
                PeliCarrusel(pelicula = pelicula, navController = navController)
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
            .clickable { navController.navigate(Routes.Info.createRoute(pelicula.id)) },
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
            modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar{
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentDestination?.hierarchy?.any { it.route == Routes.Home.route } == true,
            onClick = { navController.navigate(Routes.Home.route) }
        )

        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.List, contentDescription = "Lists") },
            label = { Text("Listas") },
            selected = currentDestination?.hierarchy?.any { it.route == Routes.Listas.route } == true,
            onClick = { navController.navigate(Routes.Listas.route) }
        )

        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications") },
            label = { Text("Notificaciones") },
            selected = currentDestination?.hierarchy?.any { it.route == Routes.Notificaciones.route } == true,
            onClick = { navController.navigate(Routes.Notificaciones.route) }
        )

        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Profile") },
            label = { Text("Perfil") },
            selected = currentDestination?.hierarchy?.any { it.route == Routes.Perfil.route } == true,
            onClick = { navController.navigate(Routes.Perfil.route) }
        )
    }
}