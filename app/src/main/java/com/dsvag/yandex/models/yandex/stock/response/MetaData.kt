package com.dsvag.yandex.models.yandex.stock.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MetaData(
    @Json(name = "exchange")
    val exchange: String,
    @Json(name = "displayName")
    val displayName: String,
    @Json(name = "id")
    val id: Long,
    @Json(name = "logoId")
    val logoId: String,
    @Json(name = "marketData")
    val marketData: MarketData,
    @Json(name = "ticker")
    val ticker: String
)