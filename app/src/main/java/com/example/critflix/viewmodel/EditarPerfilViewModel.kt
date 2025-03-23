package com.example.critflix.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.critflix.api.RetrofitClient
import com.example.critflix.model.UpdateUserRequest
import com.example.critflix.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditarPerfilViewModel : ViewModel() {
    // Estado para actualización de perfil
    private val _updateProfileState = MutableLiveData<UpdateProfileState>(UpdateProfileState.Idle)
    val updateProfileState: LiveData<UpdateProfileState> = _updateProfileState

    // Función para actualizar el perfil del usuario
    fun updateUserProfile(userId: Int, token: String, name: String, biografia: String) {
        _updateProfileState.value = UpdateProfileState.Loading

        val updateRequest = UpdateUserRequest(name, biografia)
        val apiService = RetrofitClient.getApiService(token)

        apiService.updateUser(userId, updateRequest).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful && response.body() != null) {
                    val updatedUser = response.body()!!
                    _updateProfileState.value = UpdateProfileState.Success(updatedUser)
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Error desconocido"
                    _updateProfileState.value = UpdateProfileState.Error(errorMessage)
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                _updateProfileState.value = UpdateProfileState.Error(t.message ?: "Error de conexión")
            }
        })
    }

    // Función para reiniciar el estado de actualización
    fun resetUpdateState() {
        _updateProfileState.value = UpdateProfileState.Idle
    }
}

// Estados de actualización de perfil
sealed class UpdateProfileState {
    object Idle : UpdateProfileState()
    object Loading : UpdateProfileState()
    data class Success(val user: User) : UpdateProfileState()
    data class Error(val message: String) : UpdateProfileState()
}