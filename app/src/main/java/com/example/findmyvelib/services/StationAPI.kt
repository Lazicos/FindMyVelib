package com.example.findmyvelib.services

import com.example.findmyvelib.models.InfoStation
import com.example.findmyvelib.models.StatutStation
import retrofit2.Call
import retrofit2.http.GET

interface InfoApi {
    @GET("station_information.json")
    fun getInfoStation(): Call<InfoStation>
//    fun getStatutStation(): Call<List<StatutStation>>
}

interface StatutApi {
    @GET("station_status.json")
    fun getStatutStation(): Call<StatutStation>
}