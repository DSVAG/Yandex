package com.dsvag.yandex.data.remote

import com.dsvag.yandex.models.yandex.Token
import com.dsvag.yandex.models.yandex.chart.ChartRequest
import com.dsvag.yandex.models.yandex.chart.response.ChartResponse
import com.dsvag.yandex.models.yandex.search.SearchRequest
import com.dsvag.yandex.models.yandex.search.response.SearchResponse
import com.dsvag.yandex.models.yandex.stock.StockRequest
import com.dsvag.yandex.models.yandex.stock.response.StockResponse
import com.dsvag.yandex.models.yandex.stockPrice.StockPriceRequest
import com.dsvag.yandex.models.yandex.stockPrice.response.StockInfoResponce
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface YandexApi {

    @POST("api/update_csrf")
    suspend fun getToken(): Token

    @POST("graphql")
    suspend fun search(
        @Header(X_CSRF_TOKEN) token: String,
        @Body body: SearchRequest,
    ): SearchResponse

    @POST("graphql")
    suspend fun fetchStockInfo(
        @Header(X_CSRF_TOKEN) token: String,
        @Body body: StockRequest,
    ): StockResponse

    @POST("graphql")
    suspend fun fetchStockPrice(
        @Header(X_CSRF_TOKEN) token: String,
        @Body body: StockPriceRequest,
    ): StockInfoResponce

    @POST("graphql")
    suspend fun fetchStockChart(
        @Header(X_CSRF_TOKEN) token: String,
        @Body body: ChartRequest,
    ): ChartResponse

    private companion object {
        const val X_CSRF_TOKEN = "x-csrf-token"
    }
}
