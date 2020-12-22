package com.example.photoweather.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(weather: Weather)

    @Query("Select * From weather_table Order By  id DESC")
    fun getAllWeatherHistory(): LiveData<List<Weather>>

    @Delete
    suspend fun deleteWeather(weather: Weather)
}