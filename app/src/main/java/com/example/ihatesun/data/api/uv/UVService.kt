package com.example.ihatesun.data.api.uv


import com.example.ihatesun.activity.MainActivity.Companion.coordinates
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UVService{
    private val KEY = "311c435be6054436be0d5d2e1dc7f3fd"

    val uvApiService: UVApiService = getRetrofit().create(UVApiService::class.java)

    private fun getOkHttpClient(): OkHttpClient {

        val okHttpBuilder = OkHttpClient.Builder()
        okHttpBuilder.addInterceptor(Interceptor {

            val original: Request = it.request()
            val httpUrl: HttpUrl = original.url
            val url = httpUrl.newBuilder()
                .addQueryParameter("lat", coordinates["lat"].toString())
                .addQueryParameter("lng", coordinates["lng"].toString())
                .build()

            val requestBuilder = Request.Builder()
                .addHeader("x-access-token", KEY)
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
            .baseUrl("https://api.openuv.io/api/v1/uv/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(getOkHttpClient())
            .build()
    }
}