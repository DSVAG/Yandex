package com.dsvag.yandex.models

import com.squareup.moshi.Json

data class StockSubRequest(
    @Json(name = "type")
    val type: String,

    @Json(name = "symbol")
    val symbol: String
)