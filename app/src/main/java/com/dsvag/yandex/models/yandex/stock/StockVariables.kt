package com.dsvag.yandex.models.yandex.stock


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StockVariables(
    @Json(name = "bondSchoolQuestionId")
    val bondSchoolQuestionId: String = "5b",

    @Json(name = "hasBroker")
    val hasBroker: Boolean = false,

    @Json(name = "hasIIS")
    val hasIIS: Boolean = false,

    @Json(name = "isBond")
    val isBond: Boolean = false,

    @Json(name = "isEtf")
    val isEtf: Boolean = false,

    @Json(name = "isShare")
    val isShare: Boolean = false,

    @Json(name = "slug")
    val slug: String,

    @Json(name = "type")
    val type: String = "share"
)