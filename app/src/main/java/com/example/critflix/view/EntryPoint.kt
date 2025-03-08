package com.example.critflix.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import com.example.critflix.nav.Routes
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.critflix.view.compact.*
import com.example.critflix.viewmodel.APIViewModel
import com.example.critflix.viewmodel.GenresViewModel
import com.example.critflix.viewmodel.ListViewModel
import com.example.critflix.viewmodel.SeriesViewModel

@Composable
fun EntryPoint(navigationController: NavHostController, apiViewModel: APIViewModel, seriesViewModel: SeriesViewModel, listViewModel: ListViewModel, genresViewModel: GenresViewModel) {
    NavHost(
        navController = navigationController,
        startDestination = Routes.Registro.route
    ) {
        composable(Routes.Registro.route) {
            Registro(navigationController)
        }
        composable(Routes.InicioSesion.route) {
            InicioSesion(navigationController)
        }
        composable(Routes.Home.route) {
            HomeScreen(navigationController, apiViewModel, seriesViewModel)
        }
        composable(Routes.Listas.route) {
            ListView(navigationController, apiViewModel, listViewModel)
        }
        composable(Routes.Notificaciones.route) {
            NotificationView(navigationController, apiViewModel)
        }
        composable(Routes.Perfil.route) {
            ProfileView(navigationController, apiViewModel)
        }
        composable(
            route = Routes.InfoPelis.route,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            InfoPelis(
                navController = navigationController,
                apiViewModel = apiViewModel,
                id = backStackEntry.arguments?.getInt("id") ?: 0,
                genresViewModel = genresViewModel
            )
        }

        composable(
            route = Routes.InfoSeries.route,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            InfoSeries(
                navController = navigationController,
                seriesViewModel = seriesViewModel,
                id = backStackEntry.arguments?.getInt("id") ?: 0
            )
        }
        composable(Routes.Anuncios.route) {
            Anuncios(navigationController, apiViewModel)
        }
        composable(Routes.Busqueda.route) {
            Busqueda(navigationController, apiViewModel, seriesViewModel)
        }
    }
}