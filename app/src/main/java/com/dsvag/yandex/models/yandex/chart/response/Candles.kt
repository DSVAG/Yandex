package com.dsvag.yandex.models.yandex.chart.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Candles(
    @Json(name = "seriesBefore")
    val seriesBefore: List<Pair<Long, Double>>,
)
