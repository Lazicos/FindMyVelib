package com.example.findmyvelib.services

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.google.android.gms.maps.model.LatLng
import java.io.*

/* Activté gérant la sauvegarde d'information :
*   - save() : pour sauvegarder les données d'une borne favorite
*   - show() : pour montrer le contenu du fichier favoris
*   - supprime() : pour enlever un favoris du fichier
*   - emptyFile() : met le fichier favoris à 0, utilsé pour supprime() et pour debugger
*   - checkForInternet() : vérifie si le téléphone est connecté à internet
* */

class DataGestion {
    @SuppressLint("NewApi")
    fun save(
        file: String,
        station_id: Long,
        nom: String,
        capacite: String,
        adresse: String,
        location: LatLng,
        context: Context
    ) {
        val fileOutputStream: FileOutputStream
        try {
            fileOutputStream = context.openFileOutput(file, Context.MODE_APPEND)
            // "Context.MODE_APPEND" pour rajouter des informations dans le fichier
            fileOutputStream.write(("$station_id,$nom,$capacite,$adresse,$location\n").toByteArray())
            // "/n" pour séparer les informations, on lit le fichier ligne par ligne
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun show(file: String, context: Context): ArrayList<List<String>> {
        var res = ArrayList<List<String>>()
        // valeurs retournées
        val fileInputStream: FileInputStream? = context.openFileInput(file)
        val inputStreamReader = InputStreamReader(fileInputStream)
        val bufferedReader = BufferedReader(inputStreamReader)
        val fileContent = mutableListOf<String>()
        bufferedReader.useLines { lines -> fileContent.addAll(lines) }
        // fileContent ressemble à ["0, 4", "2, 6", "0, 2"]
        for (i in 0 until fileContent.size) {
            val convert = fileContent[i].split(",").map { it.trim() }
            // convert ressemble à ["0", "2"]
            if (i == 0 && res != ArrayList<List<String>>()) {
                res = ArrayList()
            }
            res.add(convert)
            // res ressemble à [["0", "4"], ["2", "6"], ["0", "2"]]
        }
        return res
    }

    fun supprime(
        file: String,
        station_id: Long,
        context: Context
    ) {
        val data = show(file, context)
        emptyFile(file, context)
        if (data.size != 1) {
            for (i in data) {
                if (i[0] != station_id.toString()) {
                    val lat = i[6].replace("lat/lng: (", "")
                    val long = i[7].replace(")", "")
                    val latitude = lat.toDouble()
                    val longitude = long.toDouble()
                    val position = LatLng(latitude, longitude)
                    save(file, station_id, i[1], i[2], i[3] + i[4] + i[5], position, context)
                }
            }
        }
    }

    private fun emptyFile(file: String, context: Context) {
        val fileOutputStream: FileOutputStream
        try {
            fileOutputStream = context.openFileOutput(file, Context.MODE_PRIVATE)
            fileOutputStream.write("".toByteArray())
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun checkForInternet(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                // Wifi
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                // 3g etc
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                // pas d'internet
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
}