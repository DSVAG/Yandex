package com.dsvag.yandex.models.finnhub

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SocketMsg(
    @Json(name = "type")
    val type: String,

    @Json(name = "symbol")
    val symbol: String,
)