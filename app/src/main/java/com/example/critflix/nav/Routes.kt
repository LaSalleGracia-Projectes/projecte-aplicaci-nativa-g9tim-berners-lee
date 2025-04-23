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
    object CrearLista: Routes("crear_lista?listId={listId}") {
        fun createRoute(listId: String? = null) = if (listId != null) "crear_lista?listId=$listId" else "crear_lista"
    }
    object Busqueda : Routes("busqueda")
    object BusquedaConLista : Routes("busqueda/{listaId}") {
        fun createRoute(listaId: String) = "busqueda/$listaId"
    }
    object Anuncios : Routes("anuncios")
    object Ajustes : Routes("ajustes")
    object Ayuda : Routes("ayuda")
    object PoliticaPrivacidad : Routes("politica_privacidad")
    object PoliticaSeguridad : Routes("politica_seguridad")
    object PoliticaCookies : Routes("politica_cookies")
    object SolicitudCritico : Routes("solicitud_critico")
}
