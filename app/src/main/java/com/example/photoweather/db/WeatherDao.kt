package com.example.photoweather.db

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(weather: Weather)

    @Query("Select * From weather_table Order By  id DESC")
    fun getAllWeatherHistory(): LiveData<List<Weather>>
    @Query("Select img From weather_table where id =:ids ")
    fun getImage(ids :Int): LiveData<Bitmap>

    @Delete
    suspend fun deleteWeather(weather: Weather)
}