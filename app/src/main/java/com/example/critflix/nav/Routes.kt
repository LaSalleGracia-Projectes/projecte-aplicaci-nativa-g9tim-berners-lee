package com.example.critflix.nav

sealed class Routes(val route: String) {

    // COMPACT
    object PreferenciasInicio : Routes("preferencias_inicio")
    object AutentificacionCorreo : Routes("autentificacion_correo")
    object InicioSesion : Routes("inicio_sesion")
    object Registro : Routes("registro")
    object Home : Routes("home")
    object Listas : Routes("listas")
    object ContenidoListas : Routes("contenido_listas/{id}") {
        fun createRoute(id: String) = "contenido_listas/$id"
    }
    object Notificaciones : Routes("notificaciones")
    object Perfil : Routes("perfil")
    object EditarPerfil : Routes("editar_perfil")
    //TODO: Crear Info
    object InfoPelis : Routes("info_pelis/{id}") {
        fun createRoute(id: Int) = "info_pelis/$id"
    }
    object InfoSeries : Routes("info_series/{id}") {
        fun createRoute(id: Int) = "info_series/$id"
    }
    object CrearLista: Routes("crear_lista")

    object RenombrarLista : Routes("renombrar_lista/{listId}") {
        fun createRoute(listId: String) = "renombrar_lista/$listId"
    }
    object Busqueda : Routes("busqueda")
    object Anuncios : Routes("anuncios")
    object Ajustes : Routes("ajustes")
    object Ayuda : Routes("ayuda")


}