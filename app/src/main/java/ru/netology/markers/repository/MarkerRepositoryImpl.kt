package ru.netology.markers.repository

import ru.netology.markers.dto.Marker
import ru.netology.markers.entity.MarkerEntity
import ru.netology.markers.dao.MarkerDao
import androidx.lifecycle.Transformations

class MarkerRepositoryImpl(
    private val markerDao: MarkerDao,
) : MarkerRepository {
    override fun get() = Transformations.map(markerDao.getAll()) { list ->
        list.map(MarkerEntity::toDto)
    }

    override fun save(marker: Marker) {
        markerDao.insert(MarkerEntity.fromDto(marker))
    }

    override fun removeById(id: Long) {
        markerDao.removeById(id)
    }
}