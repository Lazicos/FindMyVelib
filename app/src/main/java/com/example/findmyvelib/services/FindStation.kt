package com.example.findmyvelib.services

import android.util.Log
import com.example.findmyvelib.models.InfoStation
import com.example.findmyvelib.models.StatutStation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.ArrayBlockingQueue

class FindStation {
    private var statutStation: StatutStation? = null

    fun fetchJson(id: Long): ArrayBlockingQueue<StatutStation.Data.Station>? {
        var station: StatutStation.Data.Station?
        //initiate the service
        val stationService = ServiceBuilder.buildServiceInfo(StatutApi::class.java)
        val requestCall = stationService.getStatutStation()

        val blockingQueue: ArrayBlockingQueue<StatutStation.Data.Station> =
            ArrayBlockingQueue(1) // <<<

        requestCall.enqueue(object : Callback<StatutStation> {
            override fun onResponse(
                call: Call<StatutStation>,
                response: Response<StatutStation>
            ) {
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
                    Log.d("Response", station.toString())
                    blockingQueue.add(station) // <<<
                }
            }

            override fun onFailure(call: Call<StatutStation>, t: Throwable) {
                Log.d("ResponseonFailure", "Something went wrong $t")
            }
        })

        return blockingQueue
    }


    /*fun findStatutStation(id: Long): LiveData<StatutStation.Data.Station> {
        var station = MutableLiveData<StatutStation.Data.Station>()
        //initiate the service
        val stationService = ServiceBuilder.buildServiceInfo(StatutApi::class.java)
        val requestCall = stationService.getStatutStation()
        //make network call asynchronously
        GlobalScope.launch { }
        requestCall.enqueue(object : Callback<StatutStation> {
            override fun onResponse(
                call: Call<StatutStation>,
                response: Response<StatutStation>
            ) {
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
                    station.value = stations.find { it.station_id == id }
                    Log.d("Response", station.value.toString())
                }
            }

            override fun onFailure(call: Call<StatutStation>, t: Throwable) {
                Log.d("ResponseonFailure", "Something went wrong $t")
            }
        })

        Log.d("Response1", station.value.toString())
        return station
    }*/
    /*
    fun findStatutStation(id: Long): StatutStation.Data.Station? {
        var station: StatutStation.Data.Station? = null
        //initiate the service
        val stationService = ServiceBuilder.buildServiceInfo(StatutApi::class.java)
        val requestCall = stationService.getStatutStation()
        //make network call asynchronously

            val requestResponse = requestCall.execute()
            val borne = requestResponse.body()
            val infoBorne = borne?.data?.stations
            infoBorne?.map {
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
            if (infoBorne != null) {
                station = infoBorne.find { it.station_id == id }!!
            }
            Log.d("Response", station.toString())

        Log.d("Response1", station.toString())
        return station
    }*/

/*fun findStatutStation(id : Long): LiveData<StatutStation.Data.Station> {
    var station = MutableLiveData<StatutStation.Data.Station>()
    //initiate the service
    val stationService = ServiceBuilder.buildServiceInfo(StatutApi::class.java)
    val requestCall = stationService.getStatutStation()
    //make network call asynchronously
        requestCall.enqueue(object : Callback<StatutStation> {
            override fun onResponse(
                call: Call<StatutStation>,
                response: Response<StatutStation>
            ) {
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
                    station.value = stations.find { it.station_id == id }
                    Log.d("Response", station.value.toString())
                }
            }

            override fun onFailure(call: Call<StatutStation>, t: Throwable) {
                Log.d("ResponseonFailure", "Something went wrong $t")
            }
        })

    Log.d("Response1", station.value.toString())
    return station
}*/

    fun findInfoStation(id: Long): InfoStation.Data.Station? {
        var station: InfoStation.Data.Station? = null
        //initiate the service
        val stationService = ServiceBuilder.buildServiceInfo(InfoApi::class.java)
        val requestCall = stationService.getInfoStation()
        //make network call asynchronously
        requestCall.enqueue(object : Callback<InfoStation> {
            override fun onResponse(call: Call<InfoStation>, response: Response<InfoStation>) {
                Log.d("Response", "onResponse: ${response.body()}")
                if (response.isSuccessful) {
                    val infoBorne = response.body()!!
                    Log.d("Response", "countrylist size : $infoBorne")
//                        for (location in infoBorne) {
                    val stations = infoBorne.data.stations
                    stations.map {
                        InfoStation
                            .Data.Station(
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
                }
            }

            override fun onFailure(call: Call<InfoStation>, t: Throwable) {
                Log.d("ResponseonFailure", "Something went wrong $t")
            }
        })
        return station
    }


/*fun loadInfo(): List<InfoStation.Data.Station>? {
    var station : List<InfoStation.Data.Station>? = null

    //initiate the service
    val stationService = ServiceBuilder.buildServiceInfo(InfoApi::class.java)
    val requestCall = stationService.getInfoStation()
    //make network call asynchronously
    requestCall.enqueue(object : Callback<InfoStation> {
        override fun onResponse(call: Call<InfoStation>, response: Response<InfoStation>) {
            if (response.isSuccessful) {
                val  infoBorne = response.body()!!
                Log.d("Response", "countrylist size : $infoBorne")
//                        for (location in infoBorne) {
                station = infoBorne.data.stations
                station!!.map {
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
            }
        }

        override fun onFailure(call: Call<InfoStation>, t: Throwable) {
            Log.d("ResponseonFailure", "Something went wrong $t")
        }
    })
    Log.d("Response", "onResponse: ${station}")
    return station
}*/
}