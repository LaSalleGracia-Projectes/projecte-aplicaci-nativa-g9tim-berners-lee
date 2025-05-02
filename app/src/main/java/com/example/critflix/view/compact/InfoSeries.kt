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
import com.example.critflix.model.SeriesPopulares
import com.example.critflix.model.UserSessionManager
import com.example.critflix.nav.Routes
import com.example.critflix.view.util.ActorCarousel
import com.example.critflix.viewmodel.SeriesViewModel
import com.example.critflix.viewmodel.ComentariosViewModel
import com.example.critflix.viewmodel.GenresViewModel
import com.example.critflix.viewmodel.ListViewModel
import com.example.critflix.viewmodel.RepartoViewModel
import com.example.critflix.viewmodel.ContenidoListaViewModel
import com.example.critflix.viewmodel.ValoracionesViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun InfoSeries(
    navController: NavHostController,
    seriesViewModel: SeriesViewModel,
    id: Int,
    repartoViewModel: RepartoViewModel,
    genresViewModel: GenresViewModel,
    listViewModel: ListViewModel,
    contenidoListaViewModel: ContenidoListaViewModel,
    comentariosViewModel: ComentariosViewModel,
    valoracionesViewModel: ValoracionesViewModel
) {
    val series: List<SeriesPopulares> by seriesViewModel.series.observeAsState(emptyList())
    val serie = series.find { it.id == id }
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
        repartoViewModel.getTvCredits(id)
    }

    val tvCredits by repartoViewModel.tvCredits.observeAsState()
    val isLoading by repartoViewModel.isLoading.observeAsState(initial = false)
    val error by repartoViewModel.error.observeAsState()
    val genreMap by genresViewModel.genreMap.observeAsState(emptyMap())
    val genresLoading by genresViewModel.loading.observeAsState(initial = true)

    // Función para compartir la serie
    fun shareSeries(series: SeriesPopulares) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "¡Mira esta serie: ${series.name}!")
            putExtra(Intent.EXTRA_TEXT,
                "Te recomiendo ver ${series.name}\n\n" +
                        "Sinopsis: ${series.overview}\n\n" +
                        "Valoración: ${series.vote_average}/10\n\n" +
                        "Compartido desde CritFlix"
            )
        }
        val chooser = Intent.createChooser(shareIntent, "Compartir serie")
        context.startActivity(chooser)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detalles de la serie",
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
                            serie?.let { shareSeries(it) }
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
        if (serie != null) {
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
                        model = "https://image.tmdb.org/t/p/w500${serie.backdrop_path}",
                        contentDescription = serie.name,
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
                            text = "${serie.vote_average}",
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
                        text = serie.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Popularidad: ${serie.popularity}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Fecha: ${serie.first_air_date}",
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
                            items(serie.genre_ids) { genreId ->
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
                        text = if (serie.overview.isNotEmpty()) serie.overview else "No hay sinopsis que mostrar",
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
                        tvCredits?.let { credits ->
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
                                tipo = "serie",
                                comentariosViewModel = comentariosViewModel
                            )
                        }
                        TabSeleccionada.RECOMENDACIONES -> {
                            SeccionRecomendacionesSeries(
                                serie = serie,
                                series = series,
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
                                                    tipo = "serie",
                                                    token = token
                                                )
                                                Toast.makeText(
                                                    context,
                                                    "Serie añadida a ${lista.name}",
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
fun SeccionRecomendacionesSeries(
    serie: SeriesPopulares,
    series: List<SeriesPopulares>,
    navController: NavHostController
) {

    val seriesRecomendadas = remember(serie, series) {
        series.filter { s ->
            s.id != serie.id &&
                    s.genre_ids.any { genreId -> serie.genre_ids.contains(genreId) }
        }.take(18)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Series similares",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (seriesRecomendadas.isEmpty()) {
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
                    .height((seriesRecomendadas.size / 3 * 200).dp.coerceAtMost(600.dp))
            ) {
                items(seriesRecomendadas) { serieRecomendada ->
                    SerieRecomendada(
                        serie = serieRecomendada,
                        onClick = {
                            navController.navigate(Routes.InfoSeries.createRoute(serieRecomendada.id))
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SerieRecomendada(
    serie: SeriesPopulares,
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
            model = "https://image.tmdb.org/t/p/w500${serie.poster_path}",
            contentDescription = serie.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}