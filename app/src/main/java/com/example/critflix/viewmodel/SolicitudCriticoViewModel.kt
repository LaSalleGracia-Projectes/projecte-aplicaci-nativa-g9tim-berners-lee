package com.example.critflix.viewmodel

import androidx.lifecycle.ViewModel
import com.example.critflix.api.RetrofitClient
import com.example.critflix.model.SolicitudCritico
import com.example.critflix.model.SolicitudCriticoRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SolicitudCriticoViewModel : ViewModel() {

    data class SolicitudUiState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val isSuccess: Boolean = false,
        val solicitudList: List<SolicitudCritico> = emptyList(),
        val currentSolicitud: SolicitudCritico? = null
    )

    private val _uiState = MutableStateFlow(SolicitudUiState())
    val uiState: StateFlow<SolicitudUiState> = _uiState.asStateFlow()

    fun createSolicitud(userId: Int, nombre: String, apellido: String, edad: Int, motivo: String, token: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        val apiService = RetrofitClient.getApiService(token)
        val request = SolicitudCriticoRequest(userId, nombre, apellido, edad, motivo)

        apiService.createSolicitud(request).enqueue(object : Callback<SolicitudCritico> {
            override fun onResponse(call: Call<SolicitudCritico>, response: Response<SolicitudCritico>) {
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        currentSolicitud = response.body()
                    )
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Error al enviar la solicitud"
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = errorMsg
                    )
                }
            }

            override fun onFailure(call: Call<SolicitudCritico>, t: Throwable) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error de conexión: ${t.message}"
                )
            }
        })
    }

    fun getUserSolicitudes(userId: Int, token: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        val apiService = RetrofitClient.getApiService(token)

        apiService.getUserSolicitudes(userId).enqueue(object : Callback<List<SolicitudCritico>> {
            override fun onResponse(call: Call<List<SolicitudCritico>>, response: Response<List<SolicitudCritico>>) {
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        solicitudList = response.body() ?: emptyList()
                    )
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Error al obtener las solicitudes"
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = errorMsg
                    )
                }
            }

            override fun onFailure(call: Call<List<SolicitudCritico>>, t: Throwable) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error de conexión: ${t.message}"
                )
            }
        })
    }

    fun resetState() {
        _uiState.value = SolicitudUiState()
    }
}