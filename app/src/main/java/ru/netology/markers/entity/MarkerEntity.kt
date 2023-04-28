package ru.netology.markers.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.markers.dto.Marker

@Entity
data class MarkerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val latitude: Double,
    val longitude: Double,
    val name: String,
) {
    companion object {
        fun fromDto(dto: Marker): MarkerEntity = with(dto) {
            MarkerEntity(id = id, latitude = latitude, longitude = longitude, name = name)
        }
    }

    fun toDto(): Marker = Marker(id = id, latitude = latitude, longitude = longitude, name = name)
}