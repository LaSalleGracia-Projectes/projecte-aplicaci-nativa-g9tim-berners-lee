package com.example.critflix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.example.critflix.ui.theme.CritflixTheme
import com.example.critflix.view.EntryPoint
import com.example.critflix.viewmodel.*

class MainActivity : ComponentActivity() {

    private val apiViewModel: APIViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CritflixTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navigationController = rememberNavController()
                    EntryPoint(navigationController, apiViewModel)
                }
            }
        }
    }
}
