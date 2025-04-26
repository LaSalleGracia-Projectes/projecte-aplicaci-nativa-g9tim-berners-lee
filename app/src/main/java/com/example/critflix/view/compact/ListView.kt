package com.example.critflix.view.compact

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.critflix.model.Lista
import com.example.critflix.model.PelisPopulares
import com.example.critflix.model.UserSessionManager
import com.example.critflix.nav.Routes
import com.example.critflix.viewmodel.APIViewModel
import com.example.critflix.viewmodel.ListViewModel
import com.example.critflix.viewmodel.NotificacionesViewModel

@Composable
fun ListView(navController: NavHostController, apiViewModel: APIViewModel, listViewModel: ListViewModel, notificacionesViewModel: NotificacionesViewModel) {
    var tabSeleccionado by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val userSessionManager = remember { UserSessionManager(context) }
    val token = userSessionManager.getToken() ?: ""
    val userId = userSessionManager.getUserId()

    LaunchedEffect(Unit) {
        apiViewModel.getPelis(totalMoviesNeeded = 30)
    }

    LaunchedEffect(userId) {
        if (userId > 0) {
            listViewModel.loadUserLists(userId, token)
        }
    }

    Scaffold(
        topBar = { TopBarPeliculas(tabSeleccionado) { tabSeleccionado = it } },
        bottomBar = { BottomNavigationBar(navController, notificacionesViewModel) }
    ) { padding ->
        if (tabSeleccionado == 0) {
            ContenidoPrincipal(
                peliculas = apiViewModel.pelis.observeAsState(emptyList()).value,
                paddingValues = padding
            )
        } else {
            Listas(
                paddingValues = padding,
                navController = navController,
                viewModel = listViewModel
            )
        }
    }
}

@Composable
fun TopBarPeliculas(tabSeleccionado: Int, onTabSelected: (Int) -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Black)
    ) {
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
            containerColor = Color.Black
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
            .background(color = Color.Black)
    ) {
        ContadorYBotonAnadir(cantidadPeliculas = peliculas.size)

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
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
fun TarjetaPelicula(
    pelicula: PelisPopulares,
    onFavoriteClick: () -> Unit = {}
) {
    val baseImageUrl = "https://image.tmdb.org/t/p/w185"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {/*TODO: Info pelis*/},
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
            // Poster
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(100.dp, 150.dp)
            ) {
                if (pelicula.poster_path != null) {
                    GlideImage(
                        model = "$baseImageUrl${pelicula.poster_path}",
                        contentDescription = pelicula.title,
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
                            imageVector = Icons.Default.Movie,
                            contentDescription = "Sin poster",
                            modifier = Modifier.size(40.dp),
                            tint = Color.White
                        )
                    }
                }
            }

            // Detalles
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = pelicula.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Año de lanzamiento
                if (!pelicula.release_date.isNullOrEmpty()) {
                    val year = pelicula.release_date.take(4)
                    Text(
                        text = year,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Popularidad
               Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Popularidad: ${String.format("%.1f", pelicula.popularity)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Rating
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

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tipo de contenido
                    SuggestionChip(
                        onClick = { },
                        label = {
                            Text(
                                text = "Película",
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
                                imageVector = Icons.Default.Movie,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )

                    // Botones
                    Row {
                        IconButton(
                            onClick = onFavoriteClick,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = "Favorito",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(
                            onClick = { /* TODO: Mostrar opciones */ },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Más opciones",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Listas(
    paddingValues: PaddingValues,
    navController: NavHostController,
    viewModel: ListViewModel
) {
    val context = LocalContext.current
    var expandedMenuIndex by remember { mutableStateOf<String?>(null) }
    val listas by viewModel.listas.observeAsState(emptyList())
    val userSessionManager = remember { UserSessionManager(context) }
    val token = userSessionManager.getToken() ?: ""
    val userId = userSessionManager.getUserId()
    var showDeleteConfirmation by remember { mutableStateOf<String?>(null) }
    val customListsCount = listas.count { !it.isDefault }
    val listsLimitReached = customListsCount >= viewModel.maxListas

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(color = Color.Black)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${customListsCount}/${viewModel.maxListas} Listas",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "CREAR NUEVA LISTA",
                style = MaterialTheme.typography.labelLarge,
                color = if (listsLimitReached)
                    Color.Gray
                else
                    MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable(enabled = !listsLimitReached) {
                        if (!listsLimitReached) {
                            navController.navigate(Routes.CrearLista.createRoute())
                        } else {
                            Toast.makeText(
                                context,
                                "Has alcanzado el límite máximo de listas (${viewModel.maxListas})",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .padding(vertical = 8.dp)
            )
        }
        // Diálogo de confirmación para eliminar
        if (showDeleteConfirmation != null) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = null },
                title = { Text("Confirmar eliminación") },
                text = { Text("¿Estás seguro que deseas eliminar esta lista?") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteList(showDeleteConfirmation!!, token)
                            Toast.makeText(context, "Lista eliminada", Toast.LENGTH_SHORT).show()
                            showDeleteConfirmation = null
                        }
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDeleteConfirmation = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = listas,
                key = { it.id }
            ) { lista ->
                ListContainer(
                    navController = navController,
                    lista = lista,
                    isMenuExpanded = expandedMenuIndex == lista.id,
                    onMenuClick = {
                        expandedMenuIndex = if (expandedMenuIndex == lista.id) null else lista.id
                    },
                    onListClick = {

                    },
                    onRename = {
                        navController.navigate("${Routes.CrearLista.route}/${lista.id}")
                    },
                    onDelete = {
                        showDeleteConfirmation = lista.id
                    },
                )
            }
        }
    }
}

@Composable
private fun ListContainer(
    navController: NavHostController,
    lista: Lista,
    isMenuExpanded: Boolean,
    onMenuClick: () -> Unit,
    onListClick: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate(Routes.ContenidoListas.createRoute(lista.id))
            },
        color = Color.Black,
        border = BorderStroke(2.dp, Color.Green)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = lista.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Box {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Más opciones",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = onMenuClick
                    ) {
                        // Solo mostrar opciones para listas no predeterminadas
                        if (!lista.isDefault) {
                            DropdownMenuItem(
                                text = { Text("Renombrar Critilista") },
                                onClick = {
                                    navController.navigate(Routes.CrearLista.createRoute(lista.id))
                                    onMenuClick()
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Borrar Critilista",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = {
                                    onDelete()
                                    onMenuClick()
                                }
                            )
                        } else {
                            // Para listas predeterminadas, mostrar mensaje
                            DropdownMenuItem(
                                text = { Text("Lista predeterminada (no editable)") },
                                onClick = onMenuClick,
                                enabled = false
                            )
                        }
                    }
                }
            }

            // TODO: Arreglan funcionalidad de recuento
            // TODO: Arreglar fecha de actualizacion
            Text(
                text = "${lista.itemCount} ${if (lista.itemCount == 1) "Item" else "Elementos"} • Actualizado el ${lista.lastUpdated}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

