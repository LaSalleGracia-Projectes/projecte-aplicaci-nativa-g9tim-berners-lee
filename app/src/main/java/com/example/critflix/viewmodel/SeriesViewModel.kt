package com.example.critflix.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.critflix.api.Repository
import com.example.critflix.model.SeriesPopulares
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SeriesViewModel : ViewModel() {
    private val repository = Repository()
    private val _loading = MutableLiveData(true)
    val loading: LiveData<Boolean> = _loading

    private val _series = MutableLiveData<List<SeriesPopulares>>()
    val series: LiveData<List<SeriesPopulares>> = _series

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getSeries(totalSeriesNeeded: Int = 50) {
        _loading.value = true

        val numberOfPages = (totalSeriesNeeded + 19) / 20

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val series = repository.getMultiplePagesSeries(startPage = 1, numberOfPages = numberOfPages)
                    .take(totalSeriesNeeded)
                withContext(Dispatchers.Main) {
                    _series.value = series
                    _loading.value = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _error.value = e.message ?: "Error desconocido"
                    _loading.value = false
                }
            }
        }
    }
}
