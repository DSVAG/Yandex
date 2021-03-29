package com.dsvag.yandex.models.yandex.stockPrice.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MetaData(
    @Json(name = "id")
    val id: Long,
    @Json(name = "marketData")
    val marketData: MarketData
)