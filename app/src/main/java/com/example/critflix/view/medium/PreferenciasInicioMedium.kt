package com.example.critflix.view.medium

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.critflix.model.Genre
import com.example.critflix.nav.Routes
import com.example.critflix.viewmodel.GenresViewModel

@Composable
fun PreferenciasInicioMedium(
    navHostController: NavHostController,
    genresViewModel: GenresViewModel
) {
    val backgroundColor = Color.Black
    val greenColor = Color(0xFF00FF0B)
    val darkGrayColor = Color.White.copy(alpha = 0.1f)
    val textColor = Color.Black

    val genres by genresViewModel.genres.observeAsState(emptyList())
    val loading by genresViewModel.loading.observeAsState(true)
    val error by genresViewModel.error.observeAsState("")

    val selectedGenres = remember { mutableStateListOf<Genre>() }

    val maxGenresAllowed = 5

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(bottom = 16.dp)
    ) {
        // Barra superior con título y botón de retroceso
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Selecciona tus preferencias",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
        }

        // Descripción
        Text(
            text = "Selecciona hasta 5 categorías que te interesen para personalizar tu experiencia",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Contador de selecciones
        Text(
            text = "${selectedGenres.size} de $maxGenresAllowed seleccionados",
            style = MaterialTheme.typography.bodyMedium,
            color = greenColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Grid de categorías
        if (loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = greenColor)
            }
        } else if (error.isNotEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = error,
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(genres) { genre ->
                        val isSelected = selectedGenres.contains(genre)
                        GenreItem(
                            genre = genre,
                            isSelected = isSelected,
                            onToggle = {
                                if (isSelected) {
                                    selectedGenres.remove(genre)
                                } else if (selectedGenres.size < maxGenresAllowed) {
                                    selectedGenres.add(genre)
                                }
                            },
                            enabled = isSelected || selectedGenres.size < maxGenresAllowed,
                            greenColor = greenColor,
                            darkGrayColor = darkGrayColor,
                            textColor = textColor
                        )
                    }
                }
            }
        }

        // Botón continuar
        Button(
            onClick = {
                navHostController.navigate(Routes.HomeMedium.route)
            },
            enabled = selectedGenres.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = greenColor,
                contentColor = Color.Black,
                disabledContainerColor = Color.Gray.copy(alpha = 0.5f),
                disabledContentColor = Color.White.copy(alpha = 0.5f)
            )
        ) {
            Text(
                text = "Continuar",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Continuar"
            )
        }
    }
}

@Composable
fun GenreItem(
    genre: Genre,
    isSelected: Boolean,
    onToggle: () -> Unit,
    enabled: Boolean,
    greenColor: Color,
    darkGrayColor: Color,
    textColor: Color
) {
    val backgroundColor = if (isSelected) greenColor else darkGrayColor
    val itemTextColor = if (isSelected) Color.Black else textColor
    val alpha = if (enabled) 1f else 0.5f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor.copy(alpha = alpha))
            .clickable(enabled = enabled) { onToggle() }
            .border(
                width = 1.dp,
                color = if (isSelected) greenColor else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = genre.name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = itemTextColor.copy(alpha = alpha),
            textAlign = TextAlign.Center
        )
    }
}