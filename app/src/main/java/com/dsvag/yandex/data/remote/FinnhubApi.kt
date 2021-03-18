package com.dsvag.yandex.data.remote

import com.dsvag.yandex.models.StockInfo
import com.dsvag.yandex.models.StockPriceInfo
import retrofit2.http.GET
import retrofit2.http.Query

interface FinnhubApi {
    @GET("stock/profile2")
    suspend fun fetchStockInfo(
        @Query("symbol") symbol: String
    ): StockInfo

    @GET("quote")
    suspend fun fetchCurrentPrice(
        @Query("symbol") symbol: String
    ): StockPriceInfo
}