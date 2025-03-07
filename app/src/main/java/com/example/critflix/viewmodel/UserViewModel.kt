package com.example.critflix.viewmodel

import androidx.lifecycle.ViewModel
import com.example.critflix.api.RetrofitClient
import com.example.critflix.model.LoginRequest
import com.example.critflix.model.LoginResponse
import com.example.critflix.model.RegisterRequest
import com.example.critflix.model.RegisterResponse
import com.example.critflix.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel : ViewModel() {
    // Estado para el registro
    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState.asStateFlow()

    // Estado para el inicio de sesión
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    // Función para registrar usuario
    fun registerUser(
        name: String,
        email: String,
        password: String,
        passwordConfirmation: String = password,
        fotoPerfil: String? = null,
        biografia: String? = null,
        rol: String? = null
    ) {
        if (validateRegistrationFields(name, email, password)) {
            _registrationState.value = RegistrationState.Loading

            val registerRequest = RegisterRequest(
                name,
                email,
                password,
                passwordConfirmation,
                fotoPerfil ?: "",
                biografia ?: "",
                rol ?: "usuario"
            )

            val apiService = RetrofitClient.getApiService("")

            apiService.registerUser(registerRequest).enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        _registrationState.value = RegistrationState.Success(
                            response.body()!!.token,
                            response.body()!!.user
                        )
                    } else {
                        val errorMessage = response.errorBody()?.string() ?: "Error desconocido"
                        _registrationState.value = RegistrationState.Error(errorMessage)
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    _registrationState.value = RegistrationState.Error(
                        t.message ?: "Error de conexión"
                    )
                }
            })
        }
    }

    // Función para iniciar sesión
    fun loginUser(email: String, password: String) {
        if (validateLoginFields(email, password)) {
            _loginState.value = LoginState.Loading

            val loginRequest = LoginRequest(email, password)
            val apiService = RetrofitClient.getApiService("")

            apiService.loginUser(loginRequest).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        _loginState.value = LoginState.Success(
                            response.body()!!.token,
                            response.body()!!.user
                        )
                    } else {
                        val errorMessage = when (response.code()) {
                            401 -> "Correo o contraseña incorrectos"
                            404 -> "Usuario no encontrado"
                            else -> response.errorBody()?.string() ?: "Error desconocido"
                        }
                        _loginState.value = LoginState.Error(errorMessage)
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    _loginState.value = LoginState.Error(
                        t.message ?: "Error de conexión"
                    )
                }
            })
        }
    }

    // Validación de campos de registro
    private fun validateRegistrationFields(
        name: String,
        email: String,
        password: String,
    ): Boolean {
        return when {
            name.isEmpty() -> {
                _registrationState.value = RegistrationState.Error("El nombre es obligatorio")
                false
            }
            email.isEmpty() || !email.contains("@") -> {
                _registrationState.value = RegistrationState.Error("Email inválido")
                false
            }
            password.isEmpty() || password.length < 6 -> {
                _registrationState.value = RegistrationState.Error("La contraseña debe tener al menos 6 caracteres")
                false
            }
            else -> true
        }
    }

    // Validación de campos de inicio de sesión
    private fun validateLoginFields(email: String, password: String): Boolean {
        return when {
            email.isEmpty() || !email.contains("@") -> {
                _loginState.value = LoginState.Error("Email inválido")
                false
            }
            password.isEmpty() -> {
                _loginState.value = LoginState.Error("La contraseña es obligatoria")
                false
            }
            else -> true
        }
    }
}

// Estados de registro
sealed class RegistrationState {
    object Idle : RegistrationState()
    object Loading : RegistrationState()
    data class Success(val token: String, val user: User) : RegistrationState()
    data class Error(val message: String) : RegistrationState()
}

// Estados de inicio de sesión
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val token: String, val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}