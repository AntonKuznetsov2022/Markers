package ru.netology.markers.application

import android.app.Application
import android.os.Bundle
import com.yandex.mapkit.MapKitFactory

class MarkerApplication : Application() {
    override fun onCreate() {
        MapKitFactory.setApiKey("")
        super.onCreate()
    }
}