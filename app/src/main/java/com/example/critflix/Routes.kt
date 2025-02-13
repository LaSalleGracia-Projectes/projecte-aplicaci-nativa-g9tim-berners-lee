package com.example.critflix

sealed class Routes(val route: String) {
    object Pelis : Routes("pelis")
    object Listas : Routes("listas")
    object Notificaciones : Routes("notificaciones")
    object Perfil : Routes("perfil")
}