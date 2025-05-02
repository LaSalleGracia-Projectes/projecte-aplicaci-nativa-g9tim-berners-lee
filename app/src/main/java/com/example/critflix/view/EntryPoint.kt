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
import com.example.critflix.viewmodel.BusquedaViewModel
import com.example.critflix.viewmodel.ComentariosViewModel
import com.example.critflix.viewmodel.ContenidoListaViewModel
import com.example.critflix.viewmodel.EditarPerfilViewModel
import com.example.critflix.viewmodel.GenresViewModel
import com.example.critflix.viewmodel.ListViewModel
import com.example.critflix.viewmodel.NotificacionesViewModel
import com.example.critflix.viewmodel.ProfileViewModel
import com.example.critflix.viewmodel.RepartoViewModel
import com.example.critflix.viewmodel.SeriesViewModel
import com.example.critflix.viewmodel.SolicitudCriticoViewModel
import com.example.critflix.viewmodel.UserViewModel
import com.example.critflix.viewmodel.ValoracionesViewModel

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
    busquedaViewModel: BusquedaViewModel,
    editarPerfilViewModel: EditarPerfilViewModel,
    contenidoListaViewModel: ContenidoListaViewModel,
    comentariosViewModel: ComentariosViewModel,
    notificacionesViewModel: NotificacionesViewModel,
    valoracionesViewModel: ValoracionesViewModel,
    solicitudCriticoViewModel: SolicitudCriticoViewModel,
    deviceType: String
) {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val context = LocalContext.current
    val sessionManager = remember { UserSessionManager(context) }

    when (deviceType){
        "compact" ->{
            AppNavigationCompact(navigationController, apiViewModel, seriesViewModel, listViewModel, genresViewModel,profileViewModel, repartoViewModel, userViewModel, sessionManager, busquedaViewModel, editarPerfilViewModel, contenidoListaViewModel, comentariosViewModel, notificacionesViewModel, valoracionesViewModel, solicitudCriticoViewModel)
        }
        "medium" ->{
            AppNavigationMedium(navigationController, apiViewModel, seriesViewModel, listViewModel, genresViewModel)
        }
        "expanded" ->{
            AppNavigationExpanded(navigationController, apiViewModel, seriesViewModel, listViewModel, genresViewModel)
        }
        else -> {
            AppNavigationCompact(navigationController, apiViewModel, seriesViewModel, listViewModel, genresViewModel, profileViewModel, repartoViewModel, userViewModel, sessionManager, busquedaViewModel, editarPerfilViewModel, contenidoListaViewModel, comentariosViewModel, notificacionesViewModel, valoracionesViewModel, solicitudCriticoViewModel)
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
    sessionManager: UserSessionManager,
    busquedaViewModel: BusquedaViewModel,
    editarPerfilViewModel: EditarPerfilViewModel,
    contenidoListaViewModel: ContenidoListaViewModel,
    comentariosViewModel: ComentariosViewModel,
    notificacionesViewModel: NotificacionesViewModel,
    valoracionesViewModel: ValoracionesViewModel,
    solicitudCriticoViewModel: SolicitudCriticoViewModel
){
    NavHost(
        navController = navigationController,
        startDestination = if (sessionManager.isLoggedIn()) Routes.PreferenciasInicio.route else Routes.InicioSesion.route
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
            PreferenciasInicio(navigationController, genresViewModel)
        }
        // Home
        composable(Routes.Home.route) {
            HomeScreen(navigationController, apiViewModel, seriesViewModel, genresViewModel, listViewModel, notificacionesViewModel, valoracionesViewModel)
        }
        // Listas
        composable(Routes.Listas.route) {
            ListView(navigationController, apiViewModel, seriesViewModel, listViewModel, notificacionesViewModel, valoracionesViewModel)
        }

        // Contenido Listas
        composable(
            route = Routes.ContenidoListas.route,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            ContenidoListas(
                navController = navigationController,
                listViewModel = listViewModel,
                apiViewModel = apiViewModel,
                seriesViewModel = seriesViewModel,
                id = backStackEntry.arguments?.getString("id") ?: "",
                contenidoListaViewModel = contenidoListaViewModel,
            )
        }

        // Crear Lista
        composable(
            route = Routes.CrearLista.route,
            arguments = listOf(
                navArgument("listId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            CrearLista(
                navigationController,
                listViewModel,
                backStackEntry.arguments?.getString("listId")
            )
        }

        // Notificaciones
        composable(Routes.Notificaciones.route) {
            NotificationView(navigationController, notificacionesViewModel)
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

            ProfileView(navigationController, apiViewModel, profileViewModel, userViewModel, notificacionesViewModel)
        }
        // Editar Perfil
        composable(Routes.EditarPerfil.route) {

            LaunchedEffect(Unit) {
                editarPerfilViewModel.resetUpdateState()
            }

            EditarPerfil(navigationController, profileViewModel, userViewModel, editarPerfilViewModel)
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
                repartoViewModel = repartoViewModel,
                listViewModel = listViewModel,
                contenidoListaViewModel = contenidoListaViewModel,
                comentariosViewModel = comentariosViewModel,
                valoracionesViewModel = valoracionesViewModel
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
                repartoViewModel = repartoViewModel,
                genresViewModel = genresViewModel,
                listViewModel = listViewModel,
                contenidoListaViewModel = contenidoListaViewModel,
                comentariosViewModel = comentariosViewModel,
                valoracionesViewModel = valoracionesViewModel,
            )
        }
        // Anuncios
        composable(Routes.Anuncios.route) {
            Anuncios(navigationController, apiViewModel)
        }
        // Busqueda
        composable(Routes.Busqueda.route) {
            Busqueda(navigationController, apiViewModel, seriesViewModel, busquedaViewModel, genresViewModel, contenidoListaViewModel)
        }
        // Búsqueda con ID de lista
        composable(
            route = "busqueda/{listaId}",
            arguments = listOf(navArgument("listaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val listaId = backStackEntry.arguments?.getString("listaId")
            Busqueda(
                navController = navigationController,
                apiViewModel = apiViewModel,
                seriesViewModel = seriesViewModel,
                busquedaViewModel = busquedaViewModel,
                genresViewModel = genresViewModel,
                contenidoListaViewModel = contenidoListaViewModel,
                listaId = listaId
            )
        }
        //Perfil -> Ajustes
        composable(Routes.Ajustes.route){
            Ajustes(navigationController, profileViewModel)
        }
        // Perfil -> Ayuda
        composable(Routes.Ayuda.route){
            Ayuda(navigationController)
        }
        composable(Routes.PoliticaPrivacidad.route){
            PoliticaPrivacidad(navigationController)
        }
        composable(Routes.PoliticaSeguridad.route){
            PoliticaSeguridad(navigationController)
        }
        composable(Routes.PoliticaCookies.route){
            PoliticaCookies(navigationController)
        }
        composable(Routes.SolicitudCritico.route){
            SolicitudCritico(navigationController, solicitudCriticoViewModel)
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

