package com.example.ihatesun.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ihatesun.data.api.uv.UVService
import com.example.ihatesun.data.api.weather.WeatherService
import com.example.ihatesun.model.uv.UV
import com.example.ihatesun.model.weather.Weather
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    private val _uv = MutableLiveData<UV?>()
    val uv: LiveData<UV?> = _uv

    private val _weather = MutableLiveData<Weather?>()
    val weather: LiveData<Weather?> = _weather

    private var uvService: UVService? = null
    private var weatherService: WeatherService? = null

    fun update() {
        if(uvService == null) {
            uvService = UVService()
        }
        if(weatherService == null) {
            weatherService = WeatherService()
        }

        viewModelScope.launch {
            _uv.value = uvService?.uvApiService?.getUV()?.body()
        }
        viewModelScope.launch {
            _weather.value = weatherService?.weatherApiService?.getWeather()?.body()
        }
    }
}