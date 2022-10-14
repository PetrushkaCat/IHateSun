package com.example.ihatesun.data.api.weather

import com.example.ihatesun.activity.MainActivity
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class WeatherService {
    private val KEY = "05f640c61f74971e2c8f1bef40ee8722"

    val weatherApiService: WeatherApiService = getRetrofit().create(WeatherApiService::class.java)

    private fun getOkHttpClient(): OkHttpClient {

        val okHttpBuilder = OkHttpClient.Builder()
        okHttpBuilder.addInterceptor(Interceptor {

            val original: Request = it.request()
            val httpUrl: HttpUrl = original.url
            val url = httpUrl.newBuilder()
                .addQueryParameter("lat", MainActivity.coordinates["lat"].toString())
                .addQueryParameter("lon", MainActivity.coordinates["lng"].toString())
                .addQueryParameter("appid", KEY)
                .addQueryParameter("units", "metric")
                .build()

            val requestBuilder = Request.Builder()
                .url(url)

            val request = requestBuilder.build()
            it.proceed(request)

        })
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        okHttpBuilder.addInterceptor(logging)

        return okHttpBuilder.build()
    }


    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(getOkHttpClient())
            .build()
    }
}