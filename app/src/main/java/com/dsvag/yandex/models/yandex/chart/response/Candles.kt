package com.dsvag.yandex.models.yandex.chart.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class Candles(
    @Json(name = "seriesBefore")
    val seriesBefore: List<Pair<Long, BigDecimal>>,
)
