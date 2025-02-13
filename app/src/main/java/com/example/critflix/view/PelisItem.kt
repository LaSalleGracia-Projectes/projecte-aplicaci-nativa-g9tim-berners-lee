package com.example.critflix.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.critflix.model.Data
import com.example.critflix.model.PelisPopulares
import com.example.critflix.viewmodel.APIViewModel

@Composable
fun PelisScreen(
    navController: NavHostController,
    apiViewModel: APIViewModel
) {
    val showLoading: Boolean by apiViewModel.loading.observeAsState(true)
    val pelis: Data by apiViewModel.pelis.observeAsState(Data(0, emptyList(), 0, 0))

    LaunchedEffect(Unit) {
        apiViewModel.getCharacters()
    }

    if (showLoading) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.secondary
            )
        }
    } else {
        LazyColumn {
            items(pelis.results) { peli ->
                PelisItem(peli)
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun PelisItem(pelis: PelisPopulares) {
    val baseImageUrl = "https://image.tmdb.org/t/p/w500"

    Card(
        border = BorderStroke(2.dp, Color.LightGray),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            val imageUrl = baseImageUrl + pelis.poster_path

            GlideImage(
                model = imageUrl,
                contentDescription = "Movie Poster",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(100.dp)
            )

            Text(
                text = pelis.title,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp)
            )
        }
    }
}