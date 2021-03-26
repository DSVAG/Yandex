package com.dsvag.yandex.models.yandex.stock.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Data(
    @Json(name = "instruments")
    val instruments: Instruments,
    @Json(name = "news")
    val news: News
)