package com.example.photoweather.db

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_table")
class Weather(
    var img: Bitmap? = null,
    var place: String? = "",
    var temperature: String? = "",
    var condition: String? = ""
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}