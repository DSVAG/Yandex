package com.dsvag.yandex.models.yandex.chart


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChartVariables(
    @Json(name = "candleSize")
    val candleSize: CandleSize,
    @Json(name = "count")
    val count: Int,
    @Json(name = "exchange")
    val exchange: String,
    @Json(name = "excludeTs")
    val excludeTs: Boolean = true,
    @Json(name = "instrumentId")
    val instrumentId: Long,
    @Json(name = "to")
    val to: Long? = null
)