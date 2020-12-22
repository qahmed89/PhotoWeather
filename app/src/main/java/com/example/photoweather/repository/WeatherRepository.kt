package com.example.photoweather.repository

import androidx.lifecycle.LiveData
import com.example.designapp.other.Resource
import com.example.photoweather.db.Weather
import com.example.photoweather.db.WeatherDao
import com.example.photoweather.remote.WeatherApi
import com.example.photoweather.remote.model.WeatherResponse
import javax.inject.Inject

class WeatherRepository @Inject constructor( private val api : WeatherApi , private  val dao : WeatherDao)  {


suspend fun  getWeather (lat : String , lon :String , apikey : String ) :Resource<WeatherResponse>{
    return try {

        val response = api.getWeather(lat,lon,apikey)
        if (response.isSuccessful) {
            response.body()?.let { result ->

                return@let Resource.success(result)
            } ?: Resource.error("An unknown error occured", null)

        } else {
            Resource.error("An unknown error occured", null)
        }
    } catch (e: Exception) {

            Resource.error("Couldnt reach to the  server, Check Internet Connection", null)
    }
}


    suspend fun insertRun(weather: Weather) = dao.insertRun(weather )
    suspend fun deleteRun(weather: Weather) = dao.deleteWeather(weather )

    fun getWeatherHistory () :LiveData<List<Weather>> = dao.getAllWeatherHistory()
}