package com.example.critflix.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import com.example.critflix.nav.Routes
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.critflix.model.UserSessionManager
import com.example.critflix.view.compact.*
import com.example.critflix.viewmodel.APIViewModel
import com.example.critflix.viewmodel.GenresViewModel
import com.example.critflix.viewmodel.ListViewModel
import com.example.critflix.viewmodel.ProfileViewModel
import com.example.critflix.viewmodel.RepartoViewModel
import com.example.critflix.viewmodel.SeriesViewModel
import com.example.critflix.viewmodel.UserViewModel

@Composable
fun EntryPoint(
    navigationController: NavHostController,
    apiViewModel: APIViewModel,
    seriesViewModel: SeriesViewModel,
    listViewModel: ListViewModel,
    genresViewModel: GenresViewModel,
    profileViewModel: ProfileViewModel,
    repartoViewModel: RepartoViewModel,
    userViewModel: UserViewModel,
    deviceType: String
) {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val context = LocalContext.current
    val sessionManager = remember { UserSessionManager(context) }

    when (deviceType){
        "compact" ->{
            AppNavigationCompact(navigationController, apiViewModel, seriesViewModel, listViewModel, genresViewModel,profileViewModel, repartoViewModel, userViewModel, sessionManager)
        }
        "medium" ->{
            AppNavigationMedium(navigationController, apiViewModel, seriesViewModel, listViewModel, genresViewModel)
        }
        "expanded" ->{
            AppNavigationExpanded(navigationController, apiViewModel, seriesViewModel, listViewModel, genresViewModel)
        }
        else -> {
            AppNavigationCompact(navigationController, apiViewModel, seriesViewModel, listViewModel, genresViewModel, profileViewModel, repartoViewModel, userViewModel, sessionManager)
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
    repartoViewModel: RepartoViewModel,
    userViewModel: UserViewModel,
    sessionManager: UserSessionManager
){
    NavHost(
        navController = navigationController,
        startDestination = if (sessionManager.isLoggedIn()) Routes.Home.route else Routes.InicioSesion.route
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
            val user = sessionManager.getUserData()
            val token = sessionManager.getToken()

            LaunchedEffect(Unit) {
                if (user != null) {
                    profileViewModel.setCurrentUser(user)
                }

                // Opcionalmente, refrescar los datos del usuario desde la API
                if (user != null && token != null) {
                    profileViewModel.getUserProfile(user.id, token)
                }
            }

            ProfileView(navigationController, apiViewModel, profileViewModel, userViewModel)
        }
        // Editar Perfil
        composable(Routes.EditarPerfil.route) {
            EditarPerfil(navigationController, profileViewModel, userViewModel)
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