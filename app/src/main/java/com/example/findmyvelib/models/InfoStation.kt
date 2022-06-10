package com.example.findmyvelib.models

data class InfoStation(
    val `data`: Data,
    val lastUpdatedOther: Int,
    val ttl: Int
) {
    data class Data(
        val stations: List<Station>
    ) {
        data class Station(
            val capacity: Int,
            val lat: Double,
            val lon: Double,
            val name: String,
            val rental_methods: List<String>,
            val stationCode: String,
            val station_id: Long
        )
    }
}