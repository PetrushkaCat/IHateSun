package com.example.ihatesun.model.uv

data class Result(
    val safe_exposure_time: SafeExposureTime,
    val sun_info: SunInfo,
    val ozone: Double,
    val ozone_time: String,
    val uv: Double,
    val uv_max: Double,
    val uv_max_time: String,
    val uv_time: String
)