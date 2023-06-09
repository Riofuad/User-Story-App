package com.example.userstoryapp.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.userstoryapp.R
import com.example.userstoryapp.data.remote.response.Story
import com.example.userstoryapp.databinding.ActivityMapsBinding
import com.example.userstoryapp.databinding.MapStoryContainerBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.userstoryapp.ui.viewmodels.StoryViewModel
import com.example.userstoryapp.utils.Helper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.InfoWindowAdapter,
    AdapterView.OnItemSelectedListener {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: ActivityMapsBinding
    private val storyViewModel: StoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideSystemUI()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isTiltGesturesEnabled = true

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val zoomLevel = arrayOf(
            getString(R.string.text_adapter_maps_default),
            getString(R.string.text_adapter_maps_province),
            getString(R.string.text_adapter_maps_city),
            getString(R.string.text_adapter_maps_district),
            getString(R.string.text_adapter_maps_around)
        )

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(
                applicationContext,
                android.R.layout.simple_spinner_item,
                zoomLevel
            )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.zoomType.adapter = adapter
        binding.zoomType.onItemSelectedListener = this

        storyViewModel.storyList.observe(this) { storyList ->
            for (story in storyList) {
                mMap.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                story.lat?.toDouble() ?: 0.0,
                                story.lon?.toDouble() ?: 0.0
                            )
                        )
                )?.tag = story
            }
        }

        mMap.setInfoWindowAdapter(this)
        mMap.setOnInfoWindowClickListener { marker ->
            val data: Story = marker.tag as Story
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_USER_NAME, data.name)
            intent.putExtra(DetailActivity.EXTRA_IMAGE_URL, data.photoUrl)
            intent.putExtra(DetailActivity.EXTRA_CONTENT_DESCRIPTION, data.description)
            intent.putExtra(DetailActivity.EXTRA_LATITUDE, data.lat.toString())
            intent.putExtra(DetailActivity.EXTRA_LONGITUDE, data.lon.toString())
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        getMyLocation()
        setMapStyle()

        val token = intent.getStringExtra(EXTRA_TOKEN)
        storyViewModel.loadStoryLocation(applicationContext, token!!)
        storyViewModel.storyList.observe(this) { storyList ->
            for (story in storyList) {
                mMap.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                story.lat?.toDouble() ?: 0.0,
                                story.lon?.toDouble() ?: 0.0
                            )
                        )
                )?.tag = story
            }
        }
        storyViewModel.coordinateTemp.observe(this) {
            CameraUpdateFactory.newLatLngZoom(it, 4f)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            } else {
                Toast.makeText(
                    this,
                    "Give the app access to read your location",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    storyViewModel.coordinateTemp.postValue(
                        LatLng(location.latitude, location.longitude)
                    )
                } else {
//                    storyViewModel.coordinateTemp.postValue(storyViewModel.coordinateTemp.value)
                    storyViewModel.coordinateTemp.postValue(LatLng(-5.135399, 119.423790))
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun getInfoWindow(marker: Marker): View {
        val binding = MapStoryContainerBinding.inflate(LayoutInflater.from(this))
        val data: Story = marker.tag as Story
        binding.mapsLocationLabel.text =
            Helper.parseAddressLocation(this, marker.position.latitude, marker.position.longitude)
        binding.mapsUsername.text = data.name
        binding.mapsItemPhoto.setImageBitmap(Helper.bitmapFromUrl(this, data.photoUrl))
        return binding.root
    }

    override fun getInfoContents(marker: Marker): View? {
        return null
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.maps_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style, Error: $exception")
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val level: Float = when (position) {
            0 -> 4f
            1 -> 8f
            2 -> 11f
            3 -> 14f
            4 -> 17f
            else -> 4f
        }
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                storyViewModel.coordinateTemp.value!!,
                level
            )
        )
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(-5.135399, 119.423790),
                4f
            )
        )
    }

    @Suppress("DEPRECATION")
    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    companion object {
        const val TAG = "MapsActivity"
        const val EXTRA_TOKEN = "extra_token"
    }
}