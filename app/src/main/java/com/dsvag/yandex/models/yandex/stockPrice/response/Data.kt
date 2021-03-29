package com.dsvag.yandex.models.yandex.stockPrice.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Data(
    @Json(name = "instruments")
    val instruments: Instruments
)