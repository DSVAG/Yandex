package com.dsvag.yandex.models.finnhub

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StockDataResponse(
    @Json(name = "data")
    val stockData: List<StockData> = emptyList(),

    @Json(name = "type")
    val type: String,
)