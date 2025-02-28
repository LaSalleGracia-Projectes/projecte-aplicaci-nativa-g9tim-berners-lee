package com.example.critflix.view

import android.security.keystore.BackendBusyException
import android.telephony.CellInfoNr
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.critflix.R
import com.example.critflix.Routes
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.critflix.model.PelisPopulares
import com.example.critflix.model.SeriesPopulares
import com.example.critflix.viewmodel.APIViewModel
import com.example.critflix.viewmodel.SeriesViewModel

@Composable
fun Busqueda(navController: NavHostController, apiViewModel: APIViewModel, seriesViewModel: SeriesViewModel){
    val showLoading: Boolean by apiViewModel.loading.observeAsState(true)
    val peliculas: List<PelisPopulares> by apiViewModel.pelis.observeAsState(emptyList())
    val series: List<SeriesPopulares> by seriesViewModel.series.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        apiViewModel.getPelis(totalMoviesNeeded = 30)
        seriesViewModel.getSeries(totalSeriesNeeded = 50)
    }

    Scaffold (
        topBar = { TopBarBusqueda(navController) }
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
        } else {
            ContenidoPrincipal(
                paddingValues = innerPadding,
                navController = navController,
                apiViewModel = apiViewModel,
                seriesViewModel = seriesViewModel
            )
        }
    }
}
@Composable
fun TopBarBusqueda(navController: NavHostController){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(color = Color.LightGray)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
       Icon(
           imageVector = Icons.Default.ArrowBack,
           contentDescription = "Volver",
           modifier = Modifier
               .size(24.dp)
               .clickable { navController.navigate(Routes.Home.route) }
       )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContenidoPrincipal(paddingValues: PaddingValues, navController: NavHostController, apiViewModel: APIViewModel, seriesViewModel: SeriesViewModel){
    var busqueda by remember { mutableStateOf("") }
    val peliculas: List<PelisPopulares> by apiViewModel.pelis.observeAsState(emptyList())
    val series: List<SeriesPopulares> by seriesViewModel.series.observeAsState(emptyList())

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            TextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                placeholder = {
                    Row (
                        modifier = Modifier

                    ){
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Busqueda",
                            modifier = Modifier
                                .size(24.dp)
                        )

                        Text(" Busca tus series o peliculas")
                    }
                              },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.LightGray.copy(alpha = 0.2f),
                    focusedBorderColor = Color(0xFF666666),
                    unfocusedBorderColor = Color.LightGray
                ),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Filtrar",
                modifier = Modifier
                    .size(28.dp)
                    .clickable {  }
            )
        }

        Text(
            text = "TENDENCIAS",
            style = MaterialTheme.typography.titleMedium
        )

        LazyColumn (
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ){
            items (peliculas) { pelicula ->
                Tendencias(
                    pelicula = pelicula,
                    apiViewModel = apiViewModel,
                    seriesViewModel = seriesViewModel
                )
            }
        }
    }
}

@Composable
fun Tendencias(pelicula: PelisPopulares, apiViewModel: APIViewModel, seriesViewModel: SeriesViewModel){
    Column (
        modifier = Modifier
            .fillMaxSize()
    ){
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .width(120.dp)
                .height(180.dp)
        ) {
            Text(text = pelicula.title)
        }
    }
}