package com.example.critflix.model

import android.content.Context
import android.content.SharedPreferences
import androidx.navigation.NavController
import com.example.critflix.model.User
import com.example.critflix.nav.Routes
import com.google.gson.Gson

class UserSessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()
    private val gson = Gson()

    companion object {
        private const val KEY_TOKEN = "user_token"
        private const val KEY_USER_DATA = "user_data"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    // Guardar datos de sesión del usuario
    fun saveUserSession(token: String, user: User) {
        editor.putString(KEY_TOKEN, token)
        editor.putString(KEY_USER_DATA, gson.toJson(user))
        editor.putInt(KEY_USER_ID, user.id)
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.apply()
    }

    // Obtener token
    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    // Obtener datos del usuario
    fun getUserData(): User? {
        val userJson = sharedPreferences.getString(KEY_USER_DATA, null)
        return if (userJson != null) {
            gson.fromJson(userJson, User::class.java)
        } else {
            null
        }
    }

    // Obtener ID del usuario
    fun getUserId(): Int {
        return sharedPreferences.getInt(KEY_USER_ID, -1)
    }

    // Verificar si el usuario está logueado
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // Cerrar sesión
    fun logout(navController: NavController) {
        editor.clear()
        editor.apply()
        navController.navigate(Routes.InicioSesion.route)
    }
}