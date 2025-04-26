package com.example.critflix.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.critflix.api.RetrofitClient
import com.example.critflix.model.Notification
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificacionesViewModel : ViewModel() {
    private val _notificaciones = MutableLiveData<List<Notification>>()
    val notificaciones: LiveData<List<Notification>> = _notificaciones

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun getUserNotificaciones(userId: Int, token: String) {
        _isLoading.value = true
        _error.value = null
        val apiService = RetrofitClient.getApiService(token)

        apiService.getUserNotificaciones(userId).enqueue(object : Callback<List<Notification>> {
            override fun onResponse(call: Call<List<Notification>>, response: Response<List<Notification>>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _notificaciones.value = response.body() ?: emptyList()
                    Log.d("NotificacionesVM", "Notificaciones cargadas para userId=$userId: ${_notificaciones.value?.size}")
                } else {
                    _error.value = "Error: ${response.code()}"
                    Log.e("NotificacionesVM", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Notification>>, t: Throwable) {
                _isLoading.value = false
                _error.value = t.message
                Log.e("NotificacionesVM", "Error: ${t.message}")
            }
        })
    }

    fun markAsRead(notificationId: Int, token: String) {
        _isLoading.value = true
        val apiService = RetrofitClient.getApiService(token)

        apiService.markNotificationAsRead(notificationId).enqueue(object : Callback<Notification> {
            override fun onResponse(call: Call<Notification>, response: Response<Notification>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val currentList = _notificaciones.value?.toMutableList() ?: mutableListOf()
                    val index = currentList.indexOfFirst { it.id == notificationId }
                    if (index != -1) {
                        val updatedNotification = response.body()
                        if (updatedNotification != null) {
                            currentList[index] = updatedNotification
                            _notificaciones.value = currentList
                        }
                    }
                } else {
                    _error.value = "Error: ${response.code()}"
                    Log.e("NotificacionesVM", "Error al marcar como leída: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Notification>, t: Throwable) {
                _isLoading.value = false
                _error.value = t.message
                Log.e("NotificacionesVM", "Error al marcar como leída: ${t.message}")
            }
        })
    }

    fun markAllAsRead(userId: Int, token: String) {
        _isLoading.value = true
        val apiService = RetrofitClient.getApiService(token)

        apiService.markAllNotificationsAsRead(userId).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val currentList = _notificaciones.value?.toMutableList() ?: mutableListOf()
                    val updatedList = currentList.map { it.copy(leido = true) }
                    _notificaciones.value = updatedList
                    Log.d("NotificacionesVM", "Todas las notificaciones marcadas como leídas")
                } else {
                    _error.value = "Error: ${response.code()}"
                    Log.e("NotificacionesVM", "Error al marcar todas como leídas: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                _isLoading.value = false
                _error.value = t.message
                Log.e("NotificacionesVM", "Error al marcar todas como leídas: ${t.message}")
            }
        })
    }

    fun clearError() {
        _error.value = null
    }
}