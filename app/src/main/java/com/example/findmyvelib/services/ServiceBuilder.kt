package com.example.findmyvelib.services

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {
    private const val infosStations =
        "https://velib-metropole-opendata.smoove.pro/opendata/Velib_Metropole/station_information.json/"
    private const val statutStations =
        "https://velib-metropole-opendata.smoove.pro/opendata/Velib_Metropole/station_status.json/"

    private val okHttp = OkHttpClient.Builder()

    // retrofit builder
    private val builderInfo = Retrofit
        .Builder()
        .baseUrl(infosStations)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttp.build())

    private val builderStatut = Retrofit
        .Builder()
        .baseUrl(statutStations)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttp.build())

    // créer l'instance retrofit
    private val retrofitInfo = builderInfo.build()
    private val retrofitStatut = builderStatut.build()

    fun <T> buildServiceInfo(serviceType: Class<T>): T {
        return retrofitInfo.create(serviceType)
    }

    fun <T> buildServiceStatut(serviceType: Class<T>): T {
        return retrofitStatut.create(serviceType)
    }
}