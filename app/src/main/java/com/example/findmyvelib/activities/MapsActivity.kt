package com.example.findmyvelib.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.findmyvelib.R
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

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var infoBorne: InfoStation

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
//        Log.d("Response", "countrylist size : ${infoBorne.size}")

        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)

        setUpMap()

        /*mMap.setOnMarkerClickListener { marker ->
            if (marker.isInfoWindowShown) {
                marker.hideInfoWindow()
            } else {
                marker.showInfoWindow()
            }
            true
        }*/
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

        mMap.isMyLocationEnabled = true
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
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }

        /*val findStation = FindStation()
        val stations = findStation.loadInfo()
        stations?.map {
            Log.d("Response", "On map!!!")
            val station = LatLng(it.lat, it.lon)
            placeMarkerOnMap(station, it.station_id)
        }*/

//        val (pos, id) = LoadStations.loadInfo()
        loadStations()
    }

    // permet de mettre une pastille a la position des bornes
    private fun placeMarkerOnMap(location: LatLng, id: Long) {
        // Create a MarkerOptions object and sets the user’s current location as the position for the marker
        val markerOptions = MarkerOptions()
            .position(location)
            .title(id.toString())
        Log.d("Response", "on place capteur!")

        // Add the marker to the map
        mMap.addMarker(markerOptions)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val id = marker.title
        val intent = Intent(this@MapsActivity, StationVelib::class.java)
        intent.putExtra("id", id)
        startActivity(intent)

        return false
    }

    private fun loadStations() {
        //initiate the service
        val stationService = ServiceBuilder.buildServiceInfo(InfoApi::class.java)
        val requestCall = stationService.getInfoStation()
        //make network call asynchronously
        requestCall.enqueue(object : Callback<InfoStation> {
            override fun onResponse(call: Call<InfoStation>, response: Response<InfoStation>) {
                Log.d("Response", "onResponse: ${response.body()}")
                if (response.isSuccessful) {
                    infoBorne = response.body()!!
                    Log.d("Response", "countrylist size : $infoBorne")
//                        for (location in infoBorne) {
                    val stations = infoBorne.data.stations
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
                            placeMarkerOnMap(station, it.station_id)
                        }
                }
            }

            override fun onFailure(call: Call<InfoStation>, t: Throwable) {
                Log.d("ResponseonFailure", "Something went wrong $t")
            }
        })
    }
}