package com.example.photoweather.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.designapp.other.Event
import com.example.designapp.other.Resource
import com.example.photoweather.db.Weather
import com.example.photoweather.remote.model.WeatherResponse
import com.example.photoweather.repository.WeatherRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class WeatherViewModel @ViewModelInject constructor(private  val repo :  WeatherRepository) : ViewModel() {
 val WeatherHistory =repo.getWeatherHistory()
    private  val _data: MutableLiveData<Event<Resource<WeatherResponse>>> = MutableLiveData()

    val dataWeather : LiveData<Event<Resource<WeatherResponse>>> = _data


    fun inserWeather ( weather : Weather)=viewModelScope.launch {
           repo.insertRun(weather)
    }


    fun deleteWeather ( weather : Weather)=viewModelScope.launch {
        repo.deleteRun(weather)
    }

    fun getDataWeather (lat : String , lon :String , apikey :String ) {
        _data.value = Event(Resource.loading(null))

        viewModelScope.launch {
            val response =
                repo.getWeather(lat, lon,apikey)
            _data.value = Event(response)


        }
    }
}