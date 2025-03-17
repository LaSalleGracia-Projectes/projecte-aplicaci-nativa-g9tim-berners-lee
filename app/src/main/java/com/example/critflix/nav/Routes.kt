package com.example.critflix.nav

sealed class Routes(val route: String) {
    object PreferenciasInicio : Routes("preferencias_inicio")
    object AutentificacionCorreo : Routes("autentificacion_correo")
    object InicioSesion : Routes("inicio_sesion")
    object Registro : Routes("registro")
    object Home : Routes("home")
    object Listas : Routes("listas")
    object Notificaciones : Routes("notificaciones")
    object Perfil : Routes("perfil")
    object InfoPelis : Routes("info_pelis/{id}") {
        fun createRoute(id: Int) = "info_pelis/$id"
    }
    object InfoSeries : Routes("info_series/{id}") {
        fun createRoute(id: Int) = "info_series/$id"
    }
    object Busqueda : Routes("busqueda")
    object Anuncios : Routes("anuncios")
}