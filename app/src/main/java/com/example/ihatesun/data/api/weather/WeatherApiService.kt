package com.example.ihatesun.data.api.weather

import com.example.ihatesun.model.weather.Weather
import retrofit2.Response
import retrofit2.http.GET

interface WeatherApiService {

    @GET("data/2.5/weather")
    suspend fun getWeather(): Response<Weather>
}