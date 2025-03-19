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
import com.example.critflix.viewmodel.ProfileViewModel
import com.example.critflix.viewmodel.RepartoViewModel
import com.example.critflix.viewmodel.SeriesViewModel

@Composable
fun EntryPoint(
    navigationController: NavHostController,
    apiViewModel: APIViewModel,
    seriesViewModel: SeriesViewModel,
    listViewModel: ListViewModel,
    genresViewModel: GenresViewModel,
    profileViewModel: ProfileViewModel,
    repartoViewModel: RepartoViewModel,
    deviceType: String
) {
    when (deviceType){
        "compact" ->{
            AppNavigationCompact(navigationController, apiViewModel, seriesViewModel, listViewModel, genresViewModel,profileViewModel, repartoViewModel)
        }
        "medium" ->{
            AppNavigationMedium(navigationController, apiViewModel, seriesViewModel, listViewModel, genresViewModel)
        }
        "expanded" ->{
            AppNavigationExpanded(navigationController, apiViewModel, seriesViewModel, listViewModel, genresViewModel)
        }
        else -> {
            AppNavigationCompact(navigationController, apiViewModel, seriesViewModel, listViewModel, genresViewModel, profileViewModel)
        }
    }
}

@Composable
fun AppNavigationCompact(
    navigationController: NavHostController,
    apiViewModel: APIViewModel,
    seriesViewModel: SeriesViewModel,
    listViewModel: ListViewModel,
    genresViewModel: GenresViewModel,
    profileViewModel: ProfileViewModel,
    repartoViewModel: RepartoViewModel
){
    NavHost(
        navController = navigationController,
        startDestination = Routes.Registro.route
    ) {
        // Registro
        composable(Routes.Registro.route) {
            Registro(navigationController)
        }
        // Autentificacion de correo
        composable(Routes.AutentificacionCorreo.route){
            AutentificacionCorreo(navigationController)
        }
        // Inicio de Sesion
        composable(Routes.InicioSesion.route) {
            InicioSesion(navigationController)
        }
        // Preferencias Etiquetas
        composable(Routes.PreferenciasInicio.route){
            PreferenciasInicio(navigationController)
        }
        // Home
        composable(Routes.Home.route) {
            HomeScreen(navigationController, apiViewModel, seriesViewModel)
        }
        // Listas
        composable(Routes.Listas.route) {
            ListView(navigationController, apiViewModel, listViewModel)
        }
        // Notificaciones
        composable(Routes.Notificaciones.route) {
            NotificationView(navigationController, apiViewModel)
        }
        // Perfil
        composable(Routes.Perfil.route) {
            ProfileView(navigationController, apiViewModel, profileViewModel)
        }
        // InfoPelis
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
                genresViewModel = genresViewModel,
                repartoViewModel = repartoViewModel
            )
        }
        // InfoSeries
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
                id = backStackEntry.arguments?.getInt("id") ?: 0,
                repartoViewModel = repartoViewModel
            )
        }
        // Anuncios
        composable(Routes.Anuncios.route) {
            Anuncios(navigationController, apiViewModel)
        }
        // Busqueda
        composable(Routes.Busqueda.route) {
            Busqueda(navigationController, apiViewModel, seriesViewModel)
        }
        //Perfil -> Ajustes
        composable(Routes.Ajustes.route){
            Ajustes(navigationController)
        }
        // Perfil -> Ayuda
        composable(Routes.Ayuda.route){
            Ayuda(navigationController)
        }
        
    }
}


@Composable
fun AppNavigationMedium(
    navigationController: NavHostController,
    apiViewModel: APIViewModel,
    seriesViewModel: SeriesViewModel,
    listViewModel: ListViewModel,
    genresViewModel: GenresViewModel
){


}


@Composable
fun AppNavigationExpanded(
    navigationController: NavHostController,
    apiViewModel: APIViewModel,
    seriesViewModel: SeriesViewModel,
    listViewModel: ListViewModel,
    genresViewModel: GenresViewModel
){


}