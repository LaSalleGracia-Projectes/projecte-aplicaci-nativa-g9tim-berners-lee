package com.example.critflix

sealed class Routes(val route: String) {
    object Pelis : Routes("pelis")
}