package com.example.findmyvelib.activities

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.findmyvelib.R
import com.example.findmyvelib.models.InfoStation
import com.example.findmyvelib.models.StatutStation
import com.example.findmyvelib.services.DataGestion
import com.example.findmyvelib.services.InfoApi
import com.example.findmyvelib.services.ServiceBuilder
import com.example.findmyvelib.services.StatutApi
import com.google.android.gms.maps.model.LatLng
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/* Activité affichant les détails d'une station. Elle comprend :
*   - onCreate(savedInstanceState: Bundle?) : appellée dès le chargement de l'activité. Comprend également une méthode appelant la sauvegarde ou la suppression de favoris
*   - findStatutStation(id: Long, velo: TextView, borne: TextView, elec: TextView, meca: TextView) : charge le statut de la station
*   - findInfoStation(id: Long, nom: TextView, capaciteV: TextView, capaciteB: TextView, adresse: TextView) : charge les infos de la station
* */

class StationVelib : AppCompatActivity() {
    private lateinit var context: Context

    private var nomF: String = ""
    private var adresseF: String = ""
    private var locationF: LatLng = LatLng(0.0, 0.0)
    private var capaciteF: String = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_station_velib)
        context = applicationContext

        val favorit = findViewById<ImageButton>(R.id.favoritIb)
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

        // Gestion de données
        val dataGestion = DataGestion()
        val favFile = "Favoris"

        // Afficher si la station selectionnée est déjà dans les favoris
        var allFav = dataGestion.show(favFile, context)
        var fav = false
        for (i in allFav) {
            if (i[0] == longId.toString()) {
                fav = true
            }
        }
        if (fav) {
            favorit.setImageResource(R.drawable.fav)
        } else {
            favorit.setImageResource(R.drawable.pas_fav)
        }

        // Ajout ou suppression de favoris
        favorit.setOnClickListener {
            allFav = dataGestion.show(favFile, context)
            if (allFav.size != 0) {
                var favo = false
                for (i in allFav) {
                    if (i[0] == longId.toString()) {
                        favo = true
                    }
                }
                if (favo) {
                    favorit.setImageResource(R.drawable.pas_fav)
                    dataGestion.supprime(favFile, longId, context)
                } else {
                    if (nomF == "" || capaciteF == "" || adresseF == "" || locationF == LatLng(
                            0.0,
                            0.0
                        )
                    ) {
                        Toast.makeText(
                            context,
                            "Veuillez attendre que la page charge",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        dataGestion.save(
                            favFile,
                            longId,
                            nomF,
                            capaciteF,
                            adresseF,
                            locationF,
                            context
                        )
                        favorit.setImageResource(R.drawable.fav)
                    }
                }
            } else {
                if (nomF == "" || capaciteF == "" || adresseF == "" || locationF == LatLng(
                        0.0,
                        0.0
                    )
                ) {
                    Toast.makeText(
                        context,
                        "Veuillez attendre que la page charge",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    dataGestion.save(
                        favFile,
                        longId,
                        nomF,
                        capaciteF,
                        adresseF,
                        locationF,
                        context
                    )
                    favorit.setImageResource(R.drawable.fav)
                }
            }
        }

        // Affichage de données suivant si le téléphone est hors ligne ou non
        if (dataGestion.checkForInternet(this)) {
            findStatutStation(longId, velo, borne, elec, meca)
            findInfoStation(longId, nom, capaciteV, capaciteB, adresse)
        } else {
            val allFavv = dataGestion.show(favFile, context)
            var favo = listOf<String>()
            for (i in allFavv) {
                if (i[0] == longId.toString()) {
                    favo = i
                }
            }
            nom.text = favo[1]
            adresse.text = "${favo[3]}, ${favo[4]}, ${favo[5]}"
            velo.text = "Hors ligne"
            elec.text = "Hors ligne"
            meca.text = "Hors ligne"
            borne.text = "Hors ligne"
            capaciteB.text = favo[2]
            capaciteV.text = favo[2]
        }
    }

    private fun findStatutStation(
        id: Long,
        velo: TextView,
        borne: TextView,
        elec: TextView,
        meca: TextView
    ) {
        var station: StatutStation.Data.Station?
        val stationService = ServiceBuilder.buildServiceInfo(StatutApi::class.java)
        val requestCall = stationService.getStatutStation()
        requestCall.enqueue(object : Callback<StatutStation> {
            override fun onResponse(call: Call<StatutStation>, response: Response<StatutStation>) {
                if (response.isSuccessful) {
                    val infoBorne = response.body()!!
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
                            elec.text = "$vElec"

                            val vMeca = it.mechanical
                            meca.text = "$vMeca"
                        }
                }
            }
            override fun onFailure(call: Call<StatutStation>, t: Throwable) {
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
        var station: InfoStation.Data.Station?
        val stationService = ServiceBuilder.buildServiceInfo(InfoApi::class.java)
        val requestCall = stationService.getInfoStation()
        requestCall.enqueue(object : Callback<InfoStation> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<InfoStation>, response: Response<InfoStation>) {
                if (response.isSuccessful) {
                    val infoBorne = response.body()!!
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
                    station = stations.find { it.station_id == id }
                    nomF = "${station?.name}"
                    nom.text = nomF

                    capaciteF = "/ ${station?.capacity}"
                    capaciteB.text = capaciteF
                    capaciteV.text = capaciteF

                    val addresses: List<Address>
                    val geocoder = Geocoder(context, Locale.getDefault())

                    addresses = geocoder.getFromLocation(
                        station?.lat!!,
                        station?.lon!!,
                        1
                    ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                    val address =
                        addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                    locationF = LatLng(station?.lat!!, station?.lon!!)
                    adresseF = address
                    adresse.text = address
                }
            }
            override fun onFailure(call: Call<InfoStation>, t: Throwable) {
            }
        })
    }
}