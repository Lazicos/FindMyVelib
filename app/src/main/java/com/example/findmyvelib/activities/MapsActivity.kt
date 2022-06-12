package com.example.findmyvelib.activities

import android.content.Context
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
import com.example.findmyvelib.services.DataGestion
import com.example.findmyvelib.services.InfoApi
import com.example.findmyvelib.services.ServiceBuilder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/* Activité principale gérant la carte. Elle comprend :
*   - onCreate(savedInstanceState: Bundle?) : appellée dès le chargement de l'activité
*   - onMapReady(googleMap: GoogleMap) : connexion à l'API google maps
*   - setUpMap() : met en place la carte
*   - placeMarkerOnMap(location: LatLng, id: Long, allFav: ArrayList<List<String>>) : met une pastille à l'emplacement des bornes
*   - onMarkerClick(marker: Marker) : appel de l'activité StationVelib.kt pour afficher le détail des stations lorsqu'une pastille est cliquée
*   - loadStations() : permet de charger les stations vélib. Appelle la fonction placeMarkerOnMap
* */

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var infoBorne: InfoStation

    private lateinit var context: Context

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        context = applicationContext
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        /* Un problème non résolu : si l'application plante, décommenter le paragraphe suivant, runner,
        commenter et runner à nouveau. */

        /*val dataGestion = DataGestion()
        val favFile = "Favoris"
        dataGestion.emptyFile(favFile, context)*/

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)

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

        mMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }

        val dataGestion = DataGestion()

        if (dataGestion.checkForInternet(this)) {
            loadStations()
        } else {
            val favFile = "Favoris"
            val allFav = dataGestion.show(favFile, context)
            Log.d("File", "$allFav")
            for (i in allFav) {
                val lat = i[6].replace("lat/lng: (", "")
                val long = i[7].replace(")", "")
                val latitude = lat.toDouble()
                val longitude = long.toDouble()
                val position = LatLng(latitude, longitude)
                placeMarkerOnMap(position, i[0].toLong(), allFav)
            }
        }
    }

    private fun placeMarkerOnMap(location: LatLng, id: Long, allFav: ArrayList<List<String>>) {
        // Créé un objet MarkerOptions à la position de la borne
        val markerOptions = MarkerOptions()
            .position(location)
            .title(id.toString())

        // Changement de couleur pour les marker favoris
        for (i in allFav) {
            if (i[0] == id.toString()) {
                markerOptions
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            }
        }
        // Ajout du marker sur la carte
        mMap.addMarker(markerOptions)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val id = marker.title
        val intent = Intent(context, StationVelib::class.java).apply {
            putExtra("id", id)
        }
        startActivity(intent)
        return false
    }

    private fun loadStations() {
        val dataGestion = DataGestion()
        val favFile = "Favoris"
        val allFav = dataGestion.show(favFile, context)

        //initiate the service
        val stationService = ServiceBuilder.buildServiceInfo(InfoApi::class.java)
        val requestCall = stationService.getInfoStation()
        //make network call asynchronously
        requestCall.enqueue(object : Callback<InfoStation> {
            override fun onResponse(call: Call<InfoStation>, response: Response<InfoStation>) {
                if (response.isSuccessful) {
                    infoBorne = response.body()!!
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
                            placeMarkerOnMap(station, it.station_id, allFav)
                        }
                }
            }

            override fun onFailure(call: Call<InfoStation>, t: Throwable) {
                Log.d("ResponseonFailure", "Something went wrong $t")
            }
        })
    }
}