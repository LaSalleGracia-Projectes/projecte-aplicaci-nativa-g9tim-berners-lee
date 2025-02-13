package com.example.critflix.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.critflix.api.Repository
import kotlinx.coroutines.launch
import com.example.critflix.model.Data
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class APIViewModel : ViewModel() {
    private val repository = Repository()
    private val _loading =MutableLiveData(true)
    val loading = _loading
    private val _pelis = MutableLiveData<Data>()
    val pelis = _pelis

    fun getCharacters(){
        CoroutineScope(Dispatchers.IO).launch {
            val response = repository.getAllPelis()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _pelis.value = response.body()
                    _loading.value =false
                } else {
                    Log.e("Error :", response.message())
                }
            }
        }
    }
}
