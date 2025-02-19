package com.example.critflix.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import com.example.critflix.Routes
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.critflix.viewmodel.APIViewModel

@Composable
fun EntryPoint(
    navigationController: NavHostController,
    apiViewModel: APIViewModel
) {
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
            HomeScreen(navigationController, apiViewModel)
        }
        composable(Routes.Listas.route) {
            ListView(navigationController, apiViewModel)
        }
        composable(Routes.Notificaciones.route) {
            NotificationView(navigationController, apiViewModel)
        }
        composable(Routes.Perfil.route) {
            ProfileView(navigationController, apiViewModel)
        }
        composable(
            route = Routes.Info.route,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            InfoView(
                navController = navigationController,
                apiViewModel = apiViewModel,
                id = backStackEntry.arguments?.getInt("id") ?: 0
            )
        }
        composable(Routes.Anuncios.route) {
            Anuncios(navigationController, apiViewModel)
        }
        composable(Routes.Busqueda.route) {
            Busqueda(navigationController, apiViewModel)
        }
    }
}