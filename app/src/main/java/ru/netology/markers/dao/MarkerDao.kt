package ru.netology.markers.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.markers.entity.MarkerEntity

@Dao
interface MarkerDao {
    @Query("SELECT * FROM MarkerEntity")
    fun getAll(): LiveData<List<MarkerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(marker: MarkerEntity)

    @Query("DELETE FROM MarkerEntity WHERE id = :id")
    fun removeById(id: Long)
}