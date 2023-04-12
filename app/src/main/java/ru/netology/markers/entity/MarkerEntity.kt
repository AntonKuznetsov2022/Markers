package ru.netology.markers.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yandex.mapkit.geometry.Point
import ru.netology.markers.dto.Marker

@Entity
data class MarkerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val latitude: Double,
    val longitude: Double,
    val name: String,
) {
    fun toDto() = Marker(id, Point(latitude, longitude), name)

    companion object {
        fun fromDto(dto: Marker) =
            MarkerEntity(
                dto.id,
                dto.Point.latitude,
                dto.Point.longitude,
                dto.name,
            )
    }
}