package com.dsvag.yandex.models.yandex.stock.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ComponentOf(
    @Json(name = "instrument")
    val instrument: Instrument,

    @Json(name = "otherComponentNames")
    val otherComponentNames: List<String>,

    @Json(name = "weight")
    val weight: Double
)