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
    object Anuncios : Routes("anuncios")
    object Ajustes : Routes("ajustes")
    object Ayuda : Routes("ayuda")
    object PoliticaPrivacidad : Routes("politica_privacidad")
    object PoliticaSeguridad : Routes("politica_seguridad")
    object PoliticaCookies : Routes("politica_cookies")
    object SolicitudCritico : Routes("solicitud_critico")



    // MEDIUM
    object PreferenciasInicioMedium : Routes("preferencias_inicio_medium")
    object AutentificacionCorreoMedium : Routes("autentificacion_correo_medium")
    object InicioSesionMedium : Routes("inicio_sesion_medium")
    object RegistroMedium : Routes("registro_medium")
    object HomeMedium : Routes("home_medium")
    object ListasMedium : Routes("listas_medium")
    object ContenidoListasMedium : Routes("contenido_listas_medium/{id}") {
        fun createRoute(id: String) = "contenido_listas_medium/$id"
    }
    object NotificacionesMedium : Routes("notificaciones_medium")
    object PerfilMedium : Routes("perfil_medium")
    object EditarPerfilMedium : Routes("editar_perfil_medium")
    //TODO: Crear Info
    object InfoPelisMedium : Routes("info_pelis_medium/{id}") {
        fun createRoute(id: Int) = "info_pelis_medium/$id"
    }
    object InfoSeriesMedium : Routes("info_series_medium/{id}") {
        fun createRoute(id: Int) = "info_series_medium/$id"
    }
    object CrearListaMedium: Routes("crear_lista_medium?listId={listId}") {
        fun createRoute(listId: String? = null) = if (listId != null) "crear_lista_medium?listId=$listId" else "crear_lista_medium"
    }
    object BusquedaMedium : Routes("busqueda_medium")
    object AnunciosMedium : Routes("anuncios_medium")
    object AjustesMedium : Routes("ajustes_medium")
    object AyudaMedium : Routes("ayuda_medium")
    object PoliticaPrivacidadMedium : Routes("politica_privacidad_medium")
    object PoliticaSeguridadMedium : Routes("politica_seguridad_medium")
    object PoliticaCookiesMedium : Routes("politica_cookies_medium")
    object SolicitudCriticoMedium : Routes("solicitud_critico_medium")



    // EXPANDED
    object PreferenciasInicioExpanded : Routes("preferencias_inicio_expanded")
    object AutentificacionCorreoExpanded : Routes("autentificacion_correo_expanded")
    object InicioSesionExpanded : Routes("inicio_sesion_expanded")
    object RegistroExpanded : Routes("registro_expanded")
    object HomeExpanded : Routes("home_expanded")
    object ListasExpanded : Routes("listas_expanded")
    object ContenidoListasExpanded : Routes("contenido_listas_expanded/{id}") {
        fun createRoute(id: String) = "contenido_listas_expanded/$id"
    }
    object NotificacionesExpanded : Routes("notificaciones_expanded")
    object PerfilExpanded : Routes("perfil_expanded")
    object EditarPerfilExpanded : Routes("editar_perfil_expanded")
    //TODO: Crear Info
    object InfoPelisExpanded : Routes("info_pelis_expanded/{id}") {
        fun createRoute(id: Int) = "info_pelis_expanded/$id"
    }
    object InfoSeriesExpanded : Routes("info_series_expanded/{id}") {
        fun createRoute(id: Int) = "info_series_expanded/$id"
    }
    object CrearListaExpanded: Routes("crear_lista_expanded?listId={listId}") {
        fun createRoute(listId: String? = null) = if (listId != null) "crear_lista_expanded?listId=$listId" else "crear_lista_expanded"
    }
    object BusquedaExpanded : Routes("busqueda_expanded")
    object AnunciosExpanded : Routes("anuncios_expanded")
    object AjustesExpanded : Routes("ajustes_expanded")
    object AyudaExpanded : Routes("ayuda_expanded")
    object PoliticaPrivacidadExpanded : Routes("politica_privacidad_expanded")
    object PoliticaSeguridadExpanded : Routes("politica_seguridad_expanded")
    object PoliticaCookiesExpanded : Routes("politica_cookies_expanded")
    object SolicitudCriticoExpanded : Routes("solicitud_critico_expanded")
}