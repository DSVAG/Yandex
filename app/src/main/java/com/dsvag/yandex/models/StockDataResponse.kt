package com.dsvag.yandex.models

import com.squareup.moshi.Json

data class StockDataResponse(
    @Json(name = "data")
    val stockData: List<StockData>,
)