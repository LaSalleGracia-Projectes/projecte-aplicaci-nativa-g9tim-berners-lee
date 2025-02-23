package com.example.critflix.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.critflix.model.Lista
import kotlinx.coroutines.launch

class ListViewModel : ViewModel() {
    private val _listas = MutableLiveData<List<Lista>>()
    val listas: LiveData<List<Lista>> = _listas

    private val _maxListas = 10
    val maxListas = _maxListas

    init {
        _listas.value = listOf(
            Lista(
                id = "1",
                name = "Por ver",
                itemCount = 23,
                lastUpdated = "feb 22, 2025",
                isDefault = true
            ),
            Lista(
                id = "2",
                name = "Vistos",
                itemCount = 26,
                lastUpdated = "feb 14, 2025",
                isDefault = true
            ),
            Lista(
                id = "3",
                name = "Viendo",
                itemCount = 14,
                lastUpdated = "feb 14, 2025",
                isDefault = true
            )
        )
    }

    fun createNewList(name: String) {
        if (_listas.value?.size ?: 0 < _maxListas) {
            val newList = Lista(
                id = System.currentTimeMillis().toString(),
                name = name,
                itemCount = 0,
                lastUpdated = "feb 23, 2025"
            )
            _listas.value = _listas.value?.plus(newList) ?: listOf(newList)
        }
    }

    fun deleteList(id: String) {
        _listas.value = _listas.value?.filter { it.id != id || it.isDefault }
    }

    fun renameList(id: String, newName: String) {
        _listas.value = _listas.value?.map {
            if (it.id == id && !it.isDefault) it.copy(name = newName)
            else it
        }
    }
}