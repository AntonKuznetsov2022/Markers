package ru.netology.markers.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.netology.markers.dao.MarkerDao
import ru.netology.markers.entity.MarkerEntity

@Database(entities = [MarkerEntity::class], version = 1)
abstract class AppDb : RoomDatabase() {
    abstract fun markerDAO(): MarkerDao

    companion object {
        @Volatile
        private var instance: AppDb? = null

        fun getInstance(context: Context): AppDb {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDb::class.java, "app.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
    }
}