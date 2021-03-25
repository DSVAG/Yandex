package com.dsvag.yandex.models.yandex

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Variables(
    @Json(name = "count")
    val count: Int = 25,

    @Json(name = "cursor")
    val cursor: String? = null,

    @Json(name = "searchText")
    val searchText: String,
)