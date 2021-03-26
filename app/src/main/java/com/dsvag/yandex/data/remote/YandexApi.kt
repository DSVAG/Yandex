package com.dsvag.yandex.data.remote

import com.dsvag.yandex.models.yandex.search.SearchApiRequest
import com.dsvag.yandex.models.yandex.search.response.SearchResponse
import com.dsvag.yandex.models.yandex.stock.StockApiRequest
import com.dsvag.yandex.models.yandex.stock.response.StockResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface YandexApi {
    @POST("graphql")
    suspend fun search(@Body body: SearchApiRequest): SearchResponse

    @POST("graphql?operation=Instrument")
    suspend fun fetchStockInfo(@Body body: StockApiRequest): StockResponse
}