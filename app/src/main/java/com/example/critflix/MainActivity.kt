package com.example.critflix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.compose.rememberNavController
import com.example.critflix.ui.theme.CritflixTheme
import com.example.critflix.view.EntryPoint
import com.example.critflix.viewmodel.*

class MainActivity : ComponentActivity() {
    private val apiViewModel: APIViewModel by viewModels()
    val seriesViewModel: SeriesViewModel by viewModels()
    val listaViewModel: ListViewModel by viewModels()
    val genresViewModel: GenresViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkTheme = isSystemInDarkTheme()

            CritflixTheme(
                darkTheme = isDarkTheme
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {

                    val navigationController = rememberNavController()
                    val configuration = LocalConfiguration.current
                    val deviceType = when {
                        configuration.smallestScreenWidthDp >= 840 -> "expanded"
                        configuration.smallestScreenWidthDp >= 600 -> "medium"
                        else -> "compact"
                    }

                    EntryPoint(navigationController, apiViewModel, seriesViewModel, listaViewModel, genresViewModel, deviceType)
                }
            }
        }
    }
}