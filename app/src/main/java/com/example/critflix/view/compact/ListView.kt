package com.example.critflix.view.compact

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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

@Composable
fun ListView(navController: NavHostController, apiViewModel: APIViewModel, listViewModel: ListViewModel) {
    var tabSeleccionado by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        apiViewModel.getPelis(totalMoviesNeeded = 30)
    }

    Scaffold(
        topBar = { TopBarPeliculas(tabSeleccionado) { tabSeleccionado = it } },
        bottomBar = { BottomNavigationBar(navController) }
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
fun TarjetaPelicula(pelicula: PelisPopulares) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        GlideImage(
            model = "https://image.tmdb.org/t/p/w185${pelicula.poster_path}",
            contentDescription = pelicula.title,
            modifier = Modifier
                .width(100.dp)
                .height(150.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = pelicula.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Row {
                    IconButton(onClick = { /* Marcar favorito */ }) {
                        Icon(Icons.Default.FavoriteBorder, "Favorito")
                    }
                    IconButton(onClick = { /* Mostrar opciones */ }) {
                        Icon(Icons.Default.MoreVert, "Más opciones")
                    }
                }
            }

            Text(
                text = "Fecha: ${pelicula.release_date} | ${pelicula.original_language.uppercase()}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
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
                text = "${listas.size}/${viewModel.maxListas} Listas",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "CREAR NUEVA LISTA",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable {
                        navController.navigate(Routes.CrearLista.route)
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
                    lista = lista,
                    isMenuExpanded = expandedMenuIndex == lista.id,
                    onMenuClick = {
                        expandedMenuIndex = if (expandedMenuIndex == lista.id) null else lista.id
                    },
                    onListClick = {

                    },
                    onRename = {
                        navController.navigate("${Routes.RenombrarLista.route}/${lista.id}")
                    },
                    onDelete = {
                        showDeleteConfirmation = lista.id
                    }
                )
            }
        }
    }
}

@Composable
private fun ListContainer(
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
            .clickable(onClick = onListClick),
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
                                    onRename()
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

            Text(
                text = "${lista.itemCount} ${if (lista.itemCount == 1) "Item" else "Elementos"} • Actualizado el ${lista.lastUpdated}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
