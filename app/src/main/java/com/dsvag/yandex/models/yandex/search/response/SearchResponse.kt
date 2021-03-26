package com.dsvag.yandex.models.yandex.search.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResponse(
    @Json(name = "data")
    val info: Info
)