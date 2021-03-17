package com.dsvag.yandex.models

import com.squareup.moshi.Json

data class StockInfo(
    @Json(name = "logo")
    val logo: String,

    @Json(name = "name")
    val company: String,

    @Json(name = "ticker")
    val ticker: String,
)