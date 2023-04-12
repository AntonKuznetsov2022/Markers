package ru.netology.markers.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yandex.mapkit.geometry.Point
import ru.netology.markers.dto.Marker
import ru.netology.markers.repository.MarkerRepository

private val empty = Marker(
    id = 0,
    Point(0.0, 0.0),
    name = "",
)

class MainViewModel(
    private val repository: MarkerRepository
) : ViewModel() {

    val data = repository.get()
    private val edited = MutableLiveData(empty)

    fun save() {
        edited.value?.let {
            repository.save(it)
        }
        edited.value = empty
    }

    fun edit(marker: Marker) {
        edited.value = marker
    }

    fun cancel() {
        edited.value = empty
    }

    fun getById(id: Long) = repository.getById(id)

    fun removeById(id: Long) = repository.removeById(id)
}
