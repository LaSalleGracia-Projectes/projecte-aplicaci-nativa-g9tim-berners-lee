package com.example.critflix

sealed class Routes(val route: String) {
    object InicioSesion : Routes("inicioSesion")
    object Registro : Routes("registro")
    object Home : Routes("home")
    object Listas : Routes("listas")
    object Notificaciones : Routes("notificaciones")
    object Perfil : Routes("perfil")
    object Info : Routes("info/{id}") {
        fun createRoute(id: Int) = "info/$id"
    }
    object Busqueda : Routes("busqueda")
    object Anuncios : Routes("anuncios")
}