package com.dsvag.yandex.models.yandex.stock.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MarketData(
    @Json(name = "absoluteChange")
    val absoluteChange: Double,

    @Json(name = "currencyCode")
    val currencyCode: String,

    @Json(name = "id")
    val id: String,

    @Json(name = "percentChange")
    val percentChange: Double,

    @Json(name = "price")
    val price: Double
)