package com.dsvag.yandex.models.yandex.chart.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChartResponse(
    @Json(name = "data")
    val `data`: Data
)