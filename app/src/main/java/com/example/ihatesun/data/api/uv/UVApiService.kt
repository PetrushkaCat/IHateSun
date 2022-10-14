package com.example.ihatesun.data.api.uv

import com.example.ihatesun.model.uv.UV
import retrofit2.Response
import retrofit2.http.GET

interface UVApiService {

    @GET(".")
    suspend fun getUV(): Response<UV>
}