package com.dsvag.yandex.models.yandex.search


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Info(
    @Json(name = "instruments")
    val instruments: Instruments
)