package com.dsvag.yandex.models.yandex.stock.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Related(
    @Json(name = "cursor")
    val cursor: String?,
    @Json(name = "hasMore")
    val hasMore: Boolean,
    @Json(name = "items")
    val items: List<Item>
)