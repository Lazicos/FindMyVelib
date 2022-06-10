package com.example.findmyvelib

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_station_velib)

        val nom = findViewById<TextView>(R.id.nomTv)
//        nom.text = body.toString()
    }
}