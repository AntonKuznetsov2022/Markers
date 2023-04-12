package ru.netology.markers.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import ru.netology.markers.R
import ru.netology.markers.databinding.MainActivityBinding


class MainActivity : AppCompatActivity(), UserLocationObjectListener, CameraListener {

    private var locationPermission = false
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                locationPermission = true
            } else {
                snackbarRequest()
            }
        }

    lateinit var binding: MainActivityBinding
    private lateinit var mapView: MapView
    private lateinit var userLocationLayer: UserLocationLayer
    private lateinit var mapObjectCollection: MapObjectCollection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        requestLocationPermission()
        MapKitFactory.initialize(this)

        setContentView(binding.root)
        onMapReady()

        binding.apply {
            myMarkers.setOnClickListener {
                markersList.isVisible = !markersList.isVisible
            }
            myLocation.setOnClickListener {
                if (locationPermission) {
                    cameraUserPosition()
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
        }
    }

    private fun onMapReady() {
        val mapKit = MapKitFactory.getInstance()
        mapObjectCollection = binding.mapview.map.mapObjects.addCollection()
        userLocationLayer = mapKit.createUserLocationLayer(binding.mapview.mapWindow)
        userLocationLayer.isVisible = true
        userLocationLayer.isHeadingEnabled = true

        binding.mapview.map.addCameraListener(this)
    }

    private fun cameraUserPosition() {
        if (userLocationLayer.cameraPosition() != null) {
            binding.mapview.map.move(
                CameraPosition(userLocationLayer.cameraPosition()!!.target, 15f, 0f, 0f),
                Animation(Animation.Type.SMOOTH, 1f),
                null
            )
        } else {
            Toast.makeText(this, R.string.errorLocation, Toast.LENGTH_SHORT).show()
        }
    }

    private fun snackbarRequest() {
        Snackbar.make(binding.mapview, R.string.access_to_geo, Snackbar.LENGTH_LONG)
            .setAction(R.string.permission) {
                requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }.show()
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ), 0
            )
            return
        }
    }

    override fun onStop() {
        super.onStop()
        binding.mapview.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onStart() {
        super.onStart()
        binding.mapview.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onObjectAdded(userLocationView: UserLocationView) {
        userLocationView.pin.setIcon(
            ImageProvider.fromResource(
                this,
                R.drawable.baseline_my_location_24
            )
        )
        userLocationView.arrow.setIcon(
            ImageProvider.fromResource(
                this,
                R.drawable.baseline_my_location_24
            )
        )
    }

    override fun onObjectRemoved(p0: UserLocationView) {
    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {
    }

    override fun onCameraPositionChanged(
        p0: Map,
        p1: CameraPosition,
        p2: CameraUpdateReason,
        finished: Boolean
    ) {
        if (finished) {

        }
    }

}
