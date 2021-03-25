package com.dsvag.yandex.data.remote

import com.dsvag.yandex.models.yandex.SearchRequest
import com.dsvag.yandex.models.yandex.search.SearchResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface YandexApi {
    @Headers(
        "Cookie: yandexuid=15277191614471083",
        "x-csrf-token: 5d18e9c520c9b291c431512bf0e7b1bd02874cf7:1616681978"
    )

    @POST("invest/graphql?operation=ContentForAll")
    suspend fun search(@Body searchRequest: SearchRequest): SearchResponse
}