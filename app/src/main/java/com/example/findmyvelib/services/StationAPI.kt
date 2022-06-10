package com.example.findmyvelib.services

import com.example.findmyvelib.models.InfoStation
import com.example.findmyvelib.models.StatutStation
import retrofit2.Call
import retrofit2.http.GET

interface InfoApi {
    @GET("stations")
    fun getInfoStation(): Call<List<InfoStation>>
    fun getStatutStation(): Call<List<StatutStation>>

}