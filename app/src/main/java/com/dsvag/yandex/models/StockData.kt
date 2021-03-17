package com.dsvag.yandex.models

import com.squareup.moshi.Json

data class StockData(
    @Json(name = "p")
    val lastPrice: Double,

    @Json(name = "s")
    val ticker: String,
)