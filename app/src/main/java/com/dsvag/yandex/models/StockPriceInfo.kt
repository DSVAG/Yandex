package com.dsvag.yandex.models

import com.squareup.moshi.Json

data class StockPriceInfo(
    @Json(name = "c")
    val c: Double,

    @Json(name = "t")
    val t: Int
)