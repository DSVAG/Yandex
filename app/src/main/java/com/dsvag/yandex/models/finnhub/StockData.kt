package com.dsvag.yandex.models.finnhub

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StockData(
    @Json(name = "p")
    val lastPrice: Double,

    @Json(name = "s")
    val ticker: String,
)