package com.dsvag.yandex.models.yandex.stock.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Item(
    @Json(name = "content")
    val content: String,
    @Json(name = "id")
    val id: Int,
    @Json(name = "publicationDate")
    val publicationDate: String,
    @Json(name = "source")
    val source: String,
    @Json(name = "title")
    val title: String
)