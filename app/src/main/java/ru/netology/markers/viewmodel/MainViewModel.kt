package ru.netology.markers.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.markers.db.AppDb
import ru.netology.markers.dto.Marker
import ru.netology.markers.repository.MarkerRepository
import ru.netology.markers.repository.MarkerRepositoryImpl

private val empty = Marker(
    id = 0,
    longitude = 0.0,
    latitude = 0.0,
    name = "",
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MarkerRepository =
        MarkerRepositoryImpl(AppDb.getInstance(context = application).markerDAO())

    val data = repository.get()
    private val edited = MutableLiveData(empty)

    fun save(marker: Marker) {
        edited.value?.let {
            repository.save(marker)
        }
        edited.value = empty
    }

    fun removeById(id: Long) = repository.removeById(id)
}
