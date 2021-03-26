package com.dsvag.yandex.models.yandex.search.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Catalog(
    @Json(name = "cursor")
    val cursor: String? = null,

    @Json(name = "results")
    val results: List<Result>
)