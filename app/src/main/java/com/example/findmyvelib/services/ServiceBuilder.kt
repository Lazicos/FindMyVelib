package com.example.findmyvelib.services

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {
    private const val URL =
        "https://velib-metropole-opendata.smoove.pro/opendata/Velib_Metropole/"

    private val okHttp = OkHttpClient.Builder()

    // retrofit builder
    private val builder = Retrofit
        .Builder()
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttp.build())

    // créer l'instance retrofit
    private val retrofitInfo = builder.build()

    fun <T> buildServiceInfo(serviceType: Class<T>): T {
        return retrofitInfo.create(serviceType)
    }
}