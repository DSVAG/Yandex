package com.dsvag.yandex.models.yandex.stock.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Instrument(
    @Json(name = "displayName")
    val displayName: String,
    @Json(name = "id")
    val id: String,
    @Json(name = "logoId")
    val logoId: String,
    @Json(name = "slug")
    val slug: String
)