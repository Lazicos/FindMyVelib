package com.example.findmyvelib

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.findmyvelib.databinding.ActivityMapsBinding
import com.example.findmyvelib.models.InfoStation
import com.example.findmyvelib.services.InfoApi
import com.example.findmyvelib.services.ServiceBuilder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var infoBorne: List<InfoStation>

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("Response", "countrylist size : ${infoBorne.size}")

        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        loadStations()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)

        setUpMap()
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        map.isMyLocationEnabled = true
        /* isMyLocationEnabled = true enables the my-location layer which draws a light blue dot on the user’s location.
        It also adds a button to the map that, when tapped, centers the map on the user’s location.*/

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // fusedLocationClient.getLastLocation() gives you the most recent location currently available.

            // Got last known location. In some rare situations this can be null.
            // If you were able to retrieve the the most recent location, then move the camera to the user’s current location.
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
//                placeMarkerOnMap(currentLatLng)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }


/*        val service = getInfo().create(InfosStations::class.java)
        val status = getStatut().create(StatusStation::class.java)

        runBlocking {
            val result = service.toString()
            Log.d(TAG, "synchroAPI: ${result.data.stations}")
        }*/
    }

    // permet de mettre une pastille a la position des bornes
    private fun placeMarkerOnMap(location: LatLng) {
        // Create a MarkerOptions object and sets the user’s current location as the position for the marker
        val markerOptions = MarkerOptions()
            .position(location)

        // Add the marker to the map
        map.addMarker(markerOptions)
    }

    override fun onMarkerClick(p0: Marker): Boolean = false

    private fun loadStations() {
        //initiate the service
        val stationService = ServiceBuilder.buildServiceInfo(InfoApi::class.java)
        val requestCall = stationService.getInfoStation()
        //make network call asynchronously
        requestCall.enqueue(object : Callback<List<InfoStation>> {
            override fun onResponse(
                call: Call<List<InfoStation>>,
                response: Response<List<InfoStation>>
            ) {
                Log.d("Response", "onResponse: ${response.body()}")
                if (response.isSuccessful) {
                    infoBorne = response.body()!!
                    Log.d("Response", "countrylist size : ${infoBorne.size}")
                    for (location in infoBorne) {
                        val stations = location.data.stations
                        stations.map {
                            InfoStation.Data.Station(
                                it.capacity,
                                it.lat,
                                it.lon,
                                it.name,
                                it.rental_methods,
                                it.stationCode,
                                it.station_id
                            )
                        }
                            .map {
                                val station = LatLng(it.lat, it.lon)
                                placeMarkerOnMap(station)
                            }
                    }
                } else {
                    Toast.makeText(
                        this@MapsActivity,
                        "Something went wrong ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<InfoStation>>, t: Throwable) {
                Toast.makeText(this@MapsActivity, "Something went wrong $t", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}