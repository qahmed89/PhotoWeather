package com.example.photoweather.remote

import com.example.photoweather.remote.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {


    @GET("weather")
    suspend fun getWeather(
        @Query("lat")
        lat: String,
        @Query("lon")
        lon: String,
        @Query("appid")
        appid: String
    ): Response<WeatherResponse>
}