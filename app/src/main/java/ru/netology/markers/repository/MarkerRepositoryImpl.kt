package ru.netology.markers.repository

import ru.netology.markers.dto.Marker
import ru.netology.markers.entity.MarkerEntity
import com.yandex.mapkit.geometry.Point
import ru.netology.markers.dao.MarkerDao
import androidx.lifecycle.Transformations

class MarkerRepositoryImpl(
    private val markerDao: MarkerDao,
): MarkerRepository {
    override fun get() = Transformations.map(markerDao.getAll()) { list ->
        list.map {
            Marker(it.id, Point(it.latitude, it.longitude), it.name)
        }
    }

    override fun getMarker(): Long = markerDao.getMarker().id

    override fun save(marker: Marker) {
        markerDao.save(MarkerEntity.fromDto(marker))
    }

    override fun removeById(id: Long) {
        markerDao.removeById(id)
    }

    override fun getById(id: Long): Marker {
        val entity = markerDao.findById(id)
        return Marker(entity.id, Point(entity.latitude, entity.longitude), entity.name)
    }
}