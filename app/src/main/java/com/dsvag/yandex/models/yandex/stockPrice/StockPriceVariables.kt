package com.dsvag.yandex.models.yandex.stockPrice


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StockPriceVariables(
    @Json(name = "hasBroker")
    val hasBroker: Boolean = false,
    @Json(name = "hasIIS")
    val hasIIS: Boolean = false,
    @Json(name = "slug")
    val slug: String
)