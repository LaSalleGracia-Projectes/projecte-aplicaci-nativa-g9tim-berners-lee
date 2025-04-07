package com.example.critflix.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.critflix.api.RetrofitClient
import com.example.critflix.model.Lista
import com.example.critflix.model.ListaRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ListViewModel : ViewModel() {
    private val _listas = MutableLiveData<List<Lista>>()
    val listas: LiveData<List<Lista>> = _listas
    val _createListState = MutableLiveData<CreateListState>(CreateListState.Idle)
    val createListState: LiveData<CreateListState> = _createListState
    val maxListas = 10

    // Lista de listas por defecto
    private val defaultLists = listOf(
        Lista(
            id = "default_1",
            name = "Por ver",
            itemCount = 0,
            lastUpdated = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date()),
            isDefault = true
        ),
        Lista(
            id = "default_2",
            name = "Vistos",
            itemCount = 0,
            lastUpdated = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date()),
            isDefault = true
        ),
        Lista(
            id = "default_3",
            name = "Viendo",
            itemCount = 0,
            lastUpdated = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date()),
            isDefault = true
        )
    )

    init {
        _listas.value = defaultLists
    }

    fun loadUserLists(userId: Int, token: String) {
        val apiService = RetrofitClient.getApiService(token)

        apiService.getUserLists(userId).enqueue(object : Callback<List<Lista>> {
            override fun onResponse(call: Call<List<Lista>>, response: Response<List<Lista>>) {
                if (response.isSuccessful) {
                    val userLists = response.body() ?: emptyList()
                    _listas.value = defaultLists + userLists
                } else {
                    try {
                        val gson = Gson()
                        val responseBody = response.errorBody()?.string()
                        val responseMap = gson.fromJson<Map<String, Any>>(
                            responseBody,
                            object : TypeToken<Map<String, Any>>() {}.type
                        )

                        val dataList = responseMap["data"] as? List<*>
                        if (dataList != null) {
                            val userLists = dataList.mapNotNull { item ->
                                if (item is Map<*, *>) {
                                    val listItem = item as Map<String, Any>
                                    Lista.fromApiResponse(listItem)
                                } else null
                            }
                            _listas.value = defaultLists + userLists
                        } else {
                            Log.e("ListViewModel", "Error parsing API response: $responseBody")
                        }
                    } catch (e: Exception) {
                        Log.e("ListViewModel", "Error parsing response", e)
                    }
                }
            }

            override fun onFailure(call: Call<List<Lista>>, t: Throwable) {
                Log.e("ListViewModel", "Error creating list", t)
                _createListState.value = CreateListState.Error(
                    "Error de conexión: ${t.message ?: "desconocido"}"
                )
            }
        })
    }

    fun createNewList(name: String, userId: Int, token: String) {
        Log.d("ListViewModel", "Creando lista: $name para usuario: $userId")
        if ((_listas.value?.size ?: 0) - defaultLists.size < maxListas) {
            _createListState.value = CreateListState.Loading
            
            val listaRequest = ListaRequest(
                user_id = userId,
                nombre_lista = name
            )

            val apiService = RetrofitClient.getApiService(token)

            viewModelScope.launch {
                try {
                    apiService.createList(listaRequest).enqueue(object : Callback<Lista> {
                        override fun onResponse(call: Call<Lista>, response: Response<Lista>) {
                            if (response.isSuccessful && response.body() != null) {
                                val newList = response.body()!!.copy(
                                    name = name.ifEmpty { "Nueva Lista" },  // valor por defecto
                                    lastUpdated = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
                                )
                                val currentLists = _listas.value ?: emptyList()
                                _listas.value = currentLists + newList
                                _createListState.value = CreateListState.Success(newList)
                            } else {
                                try {
                                    val gson = Gson()
                                    val responseBody = response.errorBody()?.string()

                                    if (responseBody != null) {
                                        try {
                                            val responseMap = gson.fromJson<Map<String, Any>>(
                                                responseBody,
                                                object : TypeToken<Map<String, Any>>() {}.type
                                            )

                                            val lista = Lista.fromApiResponse(responseMap).copy(
                                                name = name.ifEmpty { "Nueva Lista" },  // valor por defecto
                                                lastUpdated = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
                                            )
                                            val currentLists = _listas.value ?: emptyList()
                                            _listas.value = currentLists + lista
                                            _createListState.value = CreateListState.Success(lista)
                                        } catch (e: Exception) {
                                            Log.e("ListViewModel", "Error parsing response", e)
                                            _createListState.value = CreateListState.Error(
                                                "Error al procesar la respuesta: ${e.message}"
                                            )
                                        }
                                    } else {
                                        _createListState.value = CreateListState.Error(
                                            "Error al crear la lista: ${response.message()}"
                                        )
                                    }
                                } catch (e: Exception) {
                                    _createListState.value = CreateListState.Error(
                                        "Error: ${e.message ?: "desconocido"}"
                                    )
                                }
                            }
                        }

                        override fun onFailure(call: Call<Lista>, t: Throwable) {
                            _createListState.value = CreateListState.Error(
                                t.message ?: "Error de conexión"
                            )
                        }
                    })
                } catch (e: Exception) {
                    _createListState.value = CreateListState.Error("Error: ${e.message}")
                }
            }
        } else {
            _createListState.value = CreateListState.Error(
                "Has alcanzado el límite máximo de listas ($maxListas)"
            )
        }
    }

    fun deleteList(id: String, token: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()

        // No eliminar listas por defecto
        if (_listas.value?.find { it.id == id }?.isDefault == true) {
            result.value = false
            return result
        }

        val apiService = RetrofitClient.getApiService(token)

        apiService.deleteList(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    val currentLists = _listas.value ?: emptyList()
                    _listas.value = currentLists.filter { it.id != id || it.isDefault }
                    Log.d("ListViewModel", "Lista eliminada: $id, código: ${response.code()}")
                    result.value = true
                } else {
                    Log.e("ListViewModel", "Error eliminando lista: ${response.code()} - ${response.message()}")
                    result.value = false
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("ListViewModel", "Error eliminando lista", t)
                result.value = false
            }
        })

        return result
    }

    fun renameList(id: String, newName: String, token: String) {
        // No renombrar listas por defecto
        val lista = _listas.value?.find { it.id == id } ?: return
        if (lista.isDefault) {
            return
        }

        val userId = lista.user_id ?: return
        val listaRequest = ListaRequest(
            user_id = userId,
            nombre_lista = newName
        )

        val apiService = RetrofitClient.getApiService(token)

        apiService.updateList(id, listaRequest).enqueue(object : Callback<Lista> {
            override fun onResponse(call: Call<Lista>, response: Response<Lista>) {
                if (response.isSuccessful && response.body() != null) {
                    val updatedList = response.body()!!
                    _listas.value = _listas.value?.map {
                        if (it.id == id) updatedList else it
                    }
                }
            }

            override fun onFailure(call: Call<Lista>, t: Throwable) {
                Log.e("ListViewModel", "Error renaming list", t)
            }
        })
    }
}

sealed class CreateListState {
    object Idle : CreateListState()
    object Loading : CreateListState()
    data class Success(val lista: Lista) : CreateListState()
    data class Error(val message: String) : CreateListState()
}