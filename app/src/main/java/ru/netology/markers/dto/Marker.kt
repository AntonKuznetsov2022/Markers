package ru.netology.markers.dto

import com.yandex.mapkit.geometry.Point

data class Marker(
    val id: Long,
    val Point: Point,
    val name: String,
)