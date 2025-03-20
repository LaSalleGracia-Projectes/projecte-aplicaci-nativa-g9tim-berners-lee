package com.example.critflix.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.critflix.api.RetrofitClient
import com.example.critflix.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel: ViewModel() {
    // Estado para los datos del perfil
    private val _profileState = MutableLiveData<ProfileState>(ProfileState.Idle)
    val profileState: LiveData<ProfileState> = _profileState

    // Estado para almacenar los datos del usuario actual
    private val _currentUser = MutableLiveData<User?>(null)
    val currentUser: LiveData<User?> = _currentUser

    // Función para establecer el usuario actual desde el inicio de sesión
    fun setCurrentUser(user: User) {
        _currentUser.value = user
    }

    // Función para obtener el perfil del usuario desde la API
    fun getUserProfile(userId: Int, token: String) {
        _profileState.value = ProfileState.Loading

        val apiService = RetrofitClient.getApiService(token)

        apiService.getUserProfile(userId).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    _currentUser.value = user
                    _profileState.value = ProfileState.Success(user)
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Error desconocido"
                    _profileState.value = ProfileState.Error(errorMessage)
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                _profileState.value = ProfileState.Error(t.message ?: "Error de conexión")
            }
        })
    }
}

// Estados del perfil
sealed class ProfileState {
    object Idle : ProfileState()
    object Loading : ProfileState()
    data class Success(val user: User) : ProfileState()
    data class Error(val message: String) : ProfileState()
}