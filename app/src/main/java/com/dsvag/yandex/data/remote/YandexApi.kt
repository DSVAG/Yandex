package com.dsvag.yandex.data.remote

import com.dsvag.yandex.models.yandex.Token
import com.dsvag.yandex.models.yandex.search.SearchApiRequest
import com.dsvag.yandex.models.yandex.search.response.SearchResponse
import com.dsvag.yandex.models.yandex.stock.StockApiRequest
import com.dsvag.yandex.models.yandex.stock.response.StockResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface YandexApi {

    @POST("api/update_csrf")
    suspend fun getToken(): Token

    @POST("graphql")
    suspend fun search(
        @Header("x-csrf-token") token: String,
        @Body body: SearchApiRequest
    ): SearchResponse

    @POST("graphql")
    suspend fun fetchStockInfo(
        @Header("x-csrf-token") token: String,
        @Body body: StockApiRequest
    ): StockResponse
}