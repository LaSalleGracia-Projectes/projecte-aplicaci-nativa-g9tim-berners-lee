package com.example.critflix.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.critflix.Routes
import androidx.navigation.compose.composable
import com.example.critflix.viewmodel.APIViewModel

@Composable
fun EntryPoint(
    navigationController: NavHostController,
    apiViewModel: APIViewModel
) {
    NavHost(
        navController = navigationController,
        startDestination = Routes.Pelis.route
    ) {
        composable(Routes.Pelis.route) {
            PelisScreen(navigationController, apiViewModel)
        }
    }
}