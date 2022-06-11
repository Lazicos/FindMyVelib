package com.example.findmyvelib.activities

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.findmyvelib.R
import com.example.findmyvelib.models.InfoStation
import com.example.findmyvelib.models.StatutStation
import com.example.findmyvelib.services.InfoApi
import com.example.findmyvelib.services.ServiceBuilder
import com.example.findmyvelib.services.StatutApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


/*class UserService {
    private val retrofit = getInfo()
    private val userApi = retrofit.create(InfoApi::class.java)

    suspend fun successfulUsersResponse() {
        val usersResponse = userApi.getInfoAPI()
            .execute()
        val successful = usersResponse.isSuccessful
        val httpStatusCode = usersResponse.code()
        val httpStatusMessage = usersResponse.message()

        var body : List<InfosStations>? = usersResponse.body()
    }
}*/

class StationVelib : AppCompatActivity() {
    private lateinit var stations: StatutStation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_station_velib)

        val nom = findViewById<TextView>(R.id.nomTv)
        val adresse = findViewById<TextView>(R.id.adresseTv)
        val velo = findViewById<TextView>(R.id.veloDispoTv)
        val elec = findViewById<TextView>(R.id.elecTv)
        val meca = findViewById<TextView>(R.id.mecaTv)
        val borne = findViewById<TextView>(R.id.borneTv)
        val capaciteV = findViewById<TextView>(R.id.capaciteTv)
        val capaciteB = findViewById<TextView>(R.id.capaciteeTv)

        val id_station = intent.getSerializableExtra("id")
        val longId = id_station.toString().toLong()

        findStatutStation(longId, velo, borne, elec, meca)
        findInfoStation(longId, nom, capaciteV, capaciteB, adresse)
    }

    private fun findStatutStation(
        id: Long,
        velo: TextView,
        borne: TextView,
        elec: TextView,
        meca: TextView
    ) {
        var station: StatutStation.Data.Station? = null
        //initiate the service
        val stationService = ServiceBuilder.buildServiceInfo(StatutApi::class.java)
        val requestCall = stationService.getStatutStation()
        //make network call asynchronously
        requestCall.enqueue(object : Callback<StatutStation> {
            override fun onResponse(call: Call<StatutStation>, response: Response<StatutStation>) {
                Log.d("Response", "onResponse: ${response.body()}")
                if (response.isSuccessful) {
                    val infoBorne = response.body()!!
                    Log.d("Response", "countrylist size : $infoBorne")
//                        for (location in infoBorne) {
                    val stations = infoBorne.data.stations
                    stations.map {
                        StatutStation
                            .Data.Station(
                                it.is_installed,
                                it.is_renting,
                                it.is_returning,
                                it.last_reported,
                                it.numBikesAvailable,
                                it.numDocksAvailable,
                                it.num_bikes_available,
                                it.num_bikes_available_types,
                                it.num_docks_available,
                                it.stationCode,
                                it.station_id
                            )
                    }
                    station = stations.find { it.station_id == id }
                    velo.text = "${station?.numBikesAvailable}"
                    borne.text = "${station?.numDocksAvailable}"

                    val type = station?.num_bikes_available_types
                    type?.map {
                        StatutStation
                            .Data.Station.NumBikesAvailableType(
                                it.ebike,
                                it.mechanical
                            )
                    }
                        ?.map {
                            val vElec = it.ebike
                            elec.text = "${vElec}"

                            val vMeca = it.mechanical
                            meca.text = "${vMeca}"
                        }
                }
            }

            override fun onFailure(call: Call<StatutStation>, t: Throwable) {
                Log.d("ResponseonFailure", "Something went wrong $t")
            }
        })
    }

    private fun findInfoStation(
        id: Long,
        nom: TextView,
        capaciteV: TextView,
        capaciteB: TextView,
        adresse: TextView
    ) {
        var station: InfoStation.Data.Station? = null
        //initiate the service
        val stationService = ServiceBuilder.buildServiceInfo(InfoApi::class.java)
        val requestCall = stationService.getInfoStation()
        //make network call asynchronously
        requestCall.enqueue(object : Callback<InfoStation> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<InfoStation>, response: Response<InfoStation>) {
                Log.d("Response", "onResponse: ${response.body()}")
                if (response.isSuccessful) {
                    val infoBorne = response.body()!!
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
                    /*.map {
                        val station = LatLng(it.lat, it.lon)
                        Log.d("Response", "localisation : $station")

                        val addresses: List<Address>
                        val geocoder = Geocoder(this@StationVelib, Locale.getDefault())

                        addresses = geocoder.getFromLocation(
                            it.lat,
                            it.lon,
                            1
                        ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                        val address =
                            addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                       *//* val city = addresses[0].locality
                            val state = addresses[0].adminArea
                            val country = addresses[0].countryName
                            val postalCode = addresses[0].postalCode
                            val knownName =
                                addresses[0].featureName // Only if available else return NULL*//*

                            adresse.text = address

                            Log.d("Response", "adresse : $adresse")*/
//                        }
                    station = stations.find { it.station_id == id }
                    nom.text = "${station?.name}"
                    capaciteB.text = "/ ${station?.capacity}"
                    capaciteV.text = "/ ${station?.capacity}"

                    Log.d("Response", "localisation : $station")

                    val addresses: List<Address>
                    val geocoder = Geocoder(this@StationVelib, Locale.getDefault())

                    addresses = geocoder.getFromLocation(
                        station?.lat!!,
                        station?.lon!!,
                        1
                    ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                    val address =
                        addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                    /* val city = addresses[0].locality
                     val state = addresses[0].adminArea
                     val country = addresses[0].countryName
                     val postalCode = addresses[0].postalCode
                     val knownName =
                         addresses[0].featureName // Only if available else return NULL*/

                    adresse.text = address

                    Log.d("Response", "adresse : $adresse")

                }
            }

            override fun onFailure(call: Call<InfoStation>, t: Throwable) {
                Log.d("ResponseonFailure", "Something went wrong $t")
            }
        })
    }
}