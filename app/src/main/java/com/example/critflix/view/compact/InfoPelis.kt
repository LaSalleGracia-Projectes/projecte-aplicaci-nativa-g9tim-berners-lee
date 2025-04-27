package com.example.critflix.view.compact

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.ButtonDefaults
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.critflix.model.PelisPopulares
import com.example.critflix.model.UserSessionManager
import com.example.critflix.nav.Routes
import com.example.critflix.view.util.ActorCarousel
import com.example.critflix.viewmodel.APIViewModel
import com.example.critflix.viewmodel.ComentariosViewModel
import com.example.critflix.viewmodel.GenresViewModel
import com.example.critflix.viewmodel.ListViewModel
import com.example.critflix.viewmodel.RepartoViewModel
import com.example.critflix.viewmodel.ContenidoListaViewModel
import com.example.critflix.viewmodel.ValoracionesViewModel
import kotlinx.coroutines.launch

enum class TabSeleccionada {
    COMENTARIOS, RECOMENDACIONES
}

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun InfoPelis(navController: NavHostController, apiViewModel: APIViewModel, id: Int, genresViewModel: GenresViewModel, repartoViewModel: RepartoViewModel, listViewModel: ListViewModel, contenidoListaViewModel: ContenidoListaViewModel, comentariosViewModel: ComentariosViewModel, valoracionesViewModel: ValoracionesViewModel) {
    val peliculas: List<PelisPopulares> by apiViewModel.pelis.observeAsState(emptyList())
    val pelicula = peliculas.find { it.id == id }
    val favoritoStatusMap by valoracionesViewModel.favoritoStatusMap.observeAsState(mutableMapOf())
    val isFavorite = favoritoStatusMap[id] ?: false
    val context = LocalContext.current
    var showListsPopup by remember { mutableStateOf(false) }
    var tabSeleccionada by remember { mutableStateOf(TabSeleccionada.COMENTARIOS) }
    val coroutineScope = rememberCoroutineScope()

    val listas by listViewModel.listas.observeAsState(emptyList())
    val userSessionManager = remember { UserSessionManager(context) }
    val userId = userSessionManager.getUserId()
    val token = userSessionManager.getToken() ?: ""

    LaunchedEffect(userId) {
        if (userId > 0) {
            listViewModel.loadUserLists(userId, token)
            valoracionesViewModel.checkFavoriteStatus(userId, id, token)
        }
    }

    LaunchedEffect(id) {
        repartoViewModel.getMovieCredits(id)
    }

    val movieCredits by repartoViewModel.movieCredits.observeAsState()
    val isLoading by repartoViewModel.isLoading.observeAsState(initial = false)
    val error by repartoViewModel.error.observeAsState()
    val genreMap by genresViewModel.genreMap.observeAsState(emptyMap())
    val genresLoading by genresViewModel.loading.observeAsState(initial = true)

    // Función para compartir la película
    fun shareMovie(movie: PelisPopulares) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "¡Mira esta película: ${movie.title}!")
            putExtra(Intent.EXTRA_TEXT,
                "Te recomiendo ver ${movie.title}\n\n" +
                        "Sinopsis: ${movie.overview}\n\n" +
                        "Valoración: ${movie.vote_average}/10\n\n" +
                        "Compartido desde CritFlix"
            )
        }
        val chooser = Intent.createChooser(shareIntent, "Compartir película")
        context.startActivity(chooser)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detalles de la película",
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showListsPopup = true }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Añadir a lista",
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = { 
                            if (userId > 0) {
                                valoracionesViewModel.toggleFavorite(userId, id, token) { success ->
                                    if (success) {
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Error al actualizar favoritos",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Debes iniciar sesión para marcar favoritos",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    ) {
                        Icon(
                            if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (isFavorite) Color.Red else Color.White
                        )
                    }
                    IconButton(
                        onClick = {
                            pelicula?.let { shareMovie(it) }
                        }
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Compartir",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    actionIconContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        if (pelicula != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black)
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

                    Spacer(modifier = Modifier.height(24.dp))

                    // Tab de selección entre Comentarios y Recomendaciones
                    TabRow(
                        selectedTabIndex = if (tabSeleccionada == TabSeleccionada.COMENTARIOS) 0 else 1,
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = Color.Black,
                        contentColor = Color.Green,
                        divider = { Divider(color = Color.DarkGray) }
                    ) {
                        Tab(
                            selected = tabSeleccionada == TabSeleccionada.COMENTARIOS,
                            onClick = { tabSeleccionada = TabSeleccionada.COMENTARIOS },
                            text = {
                                Text(
                                    text = "COMENTARIOS",
                                    fontWeight = FontWeight.Bold,
                                    color = if (tabSeleccionada == TabSeleccionada.COMENTARIOS) Color.Green else Color.Gray
                                )
                            }
                        )
                        Tab(
                            selected = tabSeleccionada == TabSeleccionada.RECOMENDACIONES,
                            onClick = { tabSeleccionada = TabSeleccionada.RECOMENDACIONES },
                            text = {
                                Text(
                                    text = "RECOMENDACIONES",
                                    fontWeight = FontWeight.Bold,
                                    color = if (tabSeleccionada == TabSeleccionada.RECOMENDACIONES) Color.Green else Color.Gray
                                )
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Contenido según la pestaña seleccionada
                    when (tabSeleccionada) {
                        TabSeleccionada.COMENTARIOS -> {
                            SeccionComentarios(
                                tmdbId = id,
                                tipo = "pelicula",
                                comentariosViewModel = comentariosViewModel
                            )
                        }
                        TabSeleccionada.RECOMENDACIONES -> {
                            SeccionRecomendaciones(
                                pelicula = pelicula,
                                peliculas = peliculas,
                                navController = navController
                            )
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

        // Popup para agregar a listas
        if (showListsPopup) {
            Dialog(onDismissRequest = { showListsPopup = false }) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Black,
                    border = BorderStroke(1.dp, Color.Green)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Agregar a lista",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            textAlign = TextAlign.Center
                        )

                        if (listas.isEmpty()) {
                            Text(
                                text = "No tienes listas disponibles",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                textAlign = TextAlign.Center
                            )
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 300.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                listas.forEach { lista ->
                                    ListItem(
                                        headlineContent = {
                                            Text(
                                                text = lista.name,
                                                color = Color.White,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        },
                                        supportingContent = {
                                            Text(
                                                text = "${lista.itemCount} ${if (lista.itemCount == 1) "elemento" else "elementos"}",
                                                color = Color.Gray,
                                                fontSize = 12.sp
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                contenidoListaViewModel.addContentToList(
                                                    listaId = lista.id,
                                                    tmdbId = id,
                                                    tipo = "pelicula",
                                                    token = token
                                                )
                                                Toast.makeText(
                                                    context,
                                                    "Película añadida a ${lista.name}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                showListsPopup = false
                                            }
                                    )

                                    if (lista != listas.last()) {
                                        Divider(
                                            color = Color.DarkGray,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { showListsPopup = false },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = Color.Green
                                )
                            ) {
                                Text("Cancelar")
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            TextButton(
                                onClick = {
                                    navController.navigate(Routes.CrearLista.createRoute())
                                    showListsPopup = false
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = Color.Green
                                )
                            ) {
                                Text("Crear nueva lista")
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SeccionRecomendaciones(
    pelicula: PelisPopulares,
    peliculas: List<PelisPopulares>,
    navController: NavHostController
) {
    val peliculasRecomendadas = remember(pelicula, peliculas) {
        peliculas.filter { p ->
            p.id != pelicula.id &&
                    p.genre_ids.any { genreId -> pelicula.genre_ids.contains(genreId) }
        }.take(18)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Películas similares",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (peliculasRecomendadas.isEmpty()) {
            Text(
                text = "No hay recomendaciones disponibles",
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height((peliculasRecomendadas.size / 3 * 200).dp.coerceAtMost(600.dp))
            ) {
                items(peliculasRecomendadas) { peliculaRecomendada ->
                    PeliculaRecomendada(
                        pelicula = peliculaRecomendada,
                        onClick = {
                            navController.navigate(Routes.InfoPelis.createRoute(peliculaRecomendada.id))
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PeliculaRecomendada(
    pelicula: PelisPopulares,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2/3f)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        GlideImage(
            model = "https://image.tmdb.org/t/p/w500${pelicula.poster_path}",
            contentDescription = pelicula.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}
