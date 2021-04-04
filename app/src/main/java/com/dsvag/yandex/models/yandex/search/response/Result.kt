package com.dsvag.yandex.models.yandex.search.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Result(
    @Json(name = "id")
    val id: Long,

    @Json(name = "displayName")
    val displayName: String,

    @Json(name = "logoId")
    val logoId: String?,

    @Json(name = "marketData")
    val marketData: MarketData,

    @Json(name = "ticker")
    val ticker: String,
)