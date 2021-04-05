package com.dsvag.yandex.models.yandex.stockPrice.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MarketData(
    @Json(name = "absoluteChange")
    val absoluteChange: Double,
    @Json(name = "percentChange")
    val percentChange: Double,
    @Json(name = "price")
    val price: Double,
    @Json(name = "currencyCode")
    val currencyCode: String,
)