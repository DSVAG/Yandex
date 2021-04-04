package com.dsvag.yandex.models.yandex.chart.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Data(
    @Json(name = "candles")
    val candles: Candles
)