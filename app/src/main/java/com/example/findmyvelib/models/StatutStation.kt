package com.example.findmyvelib.models

data class StatutStation(
    val `data`: Data,
    val lastUpdatedOther: Int,
    val ttl: Int
) {
    data class Data(
        val stations: List<Station>
    ) {
        data class Station(
            val is_installed: Int,
            val is_renting: Int,
            val is_returning: Int,
            val last_reported: Int,
            val numBikesAvailable: Int,
            val numDocksAvailable: Int,
            val num_bikes_available: Int,
            val num_bikes_available_types: List<NumBikesAvailableType>,
            val num_docks_available: Int,
            val stationCode: String,
            val station_id: Long
        ) {
            data class NumBikesAvailableType(
                val ebike: Int,
                val mechanical: Int
            )
        }
    }
}