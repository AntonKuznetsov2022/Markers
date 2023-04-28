package ru.netology.markers.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputEditText
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.ui_view.ViewProvider
import ru.netology.markers.R
import ru.netology.markers.adapter.MarkerAdapter
import ru.netology.markers.adapter.OnInteractionListener
import ru.netology.markers.databinding.MainActivityBinding
import ru.netology.markers.databinding.MarkerPointBinding
import ru.netology.markers.dto.Marker
import ru.netology.markers.viewmodel.MainViewModel


class MainActivity : AppCompatActivity() {

    lateinit var binding: MainActivityBinding
    private var mapView: MapView? = null
    private lateinit var userLocationLayer: UserLocationLayer
    private lateinit var mapObjectCollection: MapObjectCollection
    val viewModel: MainViewModel by viewModels()

    companion object {
        private const val zoom = 16f
        private const val azimuth = 0F
        private const val tilt = 0F
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            when {
                granted -> {
                    userLocationLayer.isVisible = true
                    userLocationLayer.isHeadingEnabled = false
                    cameraUserPosition()
                }

                else -> {
                    Toast.makeText(this, R.string.permission, Toast.LENGTH_SHORT).show()
                }
            }
        }

    private val locationObjectListener = object : UserLocationObjectListener {
        override fun onObjectAdded(view: UserLocationView) = Unit

        override fun onObjectRemoved(view: UserLocationView) = Unit

        override fun onObjectUpdated(view: UserLocationView, event: ObjectEvent) {
            userLocationLayer.cameraPosition()?.target?.let {
                mapView?.map?.move(CameraPosition(it, zoom, azimuth, tilt))
            }
            userLocationLayer.setObjectListener(null)
        }
    }

    private val listener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) = Unit

        override fun onMapLongTap(map: Map, point: Point) {
            addMarker(point)
        }
    }

    private val placeTapListener = MapObjectTapListener { mapObject, _ ->
        viewModel.removeById(mapObject.userData as Long)
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        MapKitFactory.initialize(this)
        setContentView(binding.root)


        val adapter = MarkerAdapter(object : OnInteractionListener {
            override fun onPlace(marker: Marker) {
                moveTo(Point(marker.latitude, marker.longitude), zoom, azimuth, tilt)
            }

            override fun onRemove(marker: Marker) {
                viewModel.removeById(marker.id)
            }

            override fun onEdit(marker: Marker) {
                editNameMarker(marker)
            }
        })

        binding.markersList.adapter = adapter
        viewModel.data.observe(this) { marker ->
            adapter.submitList(marker)
        }


        binding.myLocation.setOnClickListener {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        onMapReady()

        binding.myLocation.setOnClickListener {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        binding.myMarkers.setOnClickListener {
            binding.markersList.isVisible = !binding.markersList.isVisible
        }

        mapView = binding.mapview.apply {
            map.addInputListener(listener)

            val collection = map.mapObjects.addCollection()

                viewModel.data.observe(this@MainActivity) { places ->
                    collection.clear()
                    places.forEach { place ->
                        val placeBinding = MarkerPointBinding.inflate(layoutInflater)
                        placeBinding.title.text = place.name
                        collection.addPlacemark(
                            Point(place.latitude, place.longitude),
                            ViewProvider(placeBinding.root)
                        ).apply {
                            userData = place.id
                        }
                }
            }
            collection.addTapListener(placeTapListener)
        }
    }

    private fun onMapReady() {
        val mapKit = MapKitFactory.getInstance()
        mapObjectCollection = binding.mapview.map.mapObjects.addCollection()
        userLocationLayer = mapKit.createUserLocationLayer(binding.mapview.mapWindow)
        if (checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            userLocationLayer.isVisible = true
            userLocationLayer.isHeadingEnabled = false
        }
        userLocationLayer.setObjectListener(locationObjectListener)
    }

    private fun cameraUserPosition() {
        if (userLocationLayer.cameraPosition() != null) {
            binding.mapview.map.move(
                CameraPosition(userLocationLayer.cameraPosition()!!.target, zoom, azimuth, tilt),
                Animation(Animation.Type.SMOOTH, 1f),
                null
            )
        } else {
            Toast.makeText(this, R.string.errorLocation, Toast.LENGTH_SHORT).show()
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

    private fun addMarker(point: Point) {
        val alertDialog: androidx.appcompat.app.AlertDialog = this.let {
            val builder = androidx.appcompat.app.AlertDialog.Builder(it)
            builder.apply {
                setMessage(getString(R.string.enter_name))
                val textInput =
                    TextInputEditText(this@MainActivity).apply { setText(R.string.new_place) }
                setView(textInput)

                setPositiveButton(R.string.ok) { _, _ ->
                    viewModel.save(
                        Marker(
                            id = 0L,
                            latitude = point.latitude,
                            longitude = point.longitude,
                            name = textInput.text.toString(),
                        )
                    )
                    //marker(point)
                }

                setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
            }
            builder.create()
        }
        alertDialog.show()
    }

    private fun editNameMarker(marker: Marker) {
        val alertDialog: androidx.appcompat.app.AlertDialog = this.let {
            val builder = androidx.appcompat.app.AlertDialog.Builder(it)
            builder.apply {
                setMessage(getString(R.string.enter_name))
                val textInput =
                    TextInputEditText(this@MainActivity).apply { setText(marker.name) }
                setView(textInput)

                setPositiveButton(R.string.ok) { _, _ ->
                    viewModel.save(
                        Marker(
                            id = marker.id,
                            latitude = marker.latitude,
                            longitude = marker.longitude,
                            name = textInput.text.toString(),
                        )
                    )
                }

                setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
            }
            builder.create()
        }
        alertDialog.show()
    }

    fun moveTo(point: Point, zoom: Float, azimuth: Float, tilt: Float) {
        binding.mapview.map.move(
            CameraPosition(point, zoom, azimuth, tilt),
            Animation(Animation.Type.SMOOTH, 2f),
            null
        )
    }

}
