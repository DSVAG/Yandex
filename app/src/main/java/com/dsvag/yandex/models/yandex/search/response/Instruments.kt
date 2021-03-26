package com.dsvag.yandex.models.yandex.search.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Instruments(
    @Json(name = "list")
    val catalog: Catalog
)