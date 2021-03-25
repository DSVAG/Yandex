package com.dsvag.yandex.models.finnhub

import com.squareup.moshi.Json

data class StockDataResponse(
    @Json(name = "data")
    val stockData: List<StockData> = emptyList(),

    @Json(name = "type")
    val type: String
)