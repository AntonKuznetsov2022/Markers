package ru.netology.markers.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.netology.markers.entity.MarkerEntity

@Dao
interface MarkerDao {
    @Query("SELECT * FROM MarkerEntity ORDER BY id DESC")
    fun getAll(): LiveData<List<MarkerEntity>>

    @Query("SELECT * FROM MarkerEntity ORDER BY id DESC")
    fun getMarker(): MarkerEntity

    @Query("SELECT * FROM MarkerEntity WHERE id = :id")
    fun findById(id: Long) : MarkerEntity

    @Insert
    fun insert(marker: MarkerEntity)

    @Query("UPDATE MarkerEntity SET name = :name WHERE id = :id")
    fun updateContentByID(id: Long, name: String)

    fun save(marker: MarkerEntity) =
        if (marker.id == 0L) insert(marker) else updateContentByID(marker.id, marker.name)

    @Query("DELETE FROM MarkerEntity WHERE id = :id")
    fun removeById(id: Long)
}