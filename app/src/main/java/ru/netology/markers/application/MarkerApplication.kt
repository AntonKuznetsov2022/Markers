package ru.netology.markers.application

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import ru.netology.markers.BuildConfig

class MarkerApplication : Application() {
    override fun onCreate() {
        MapKitFactory.setApiKey(BuildConfig.MAPS_API_KEY)
        super.onCreate()
    }
}