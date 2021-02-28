package com.dsvag.yandex.data.repositoyes

import com.dsvag.yandex.data.local.StockInfoDao
import com.dsvag.yandex.data.remote.ApiFinnhubService
import com.dsvag.yandex.data.remote.StockWebSocketListener
import com.dsvag.yandex.models.StockSubRequest
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.WebSocket
import javax.inject.Inject

class StockRepository @Inject constructor(
    private val stockWebSocketListener: StockWebSocketListener,
    private val webSocket: WebSocket,
    private val apiFinnhubService: ApiFinnhubService,
    private val stockInfoDao: StockInfoDao,
) {
    val stockChangeFlow get() = stockWebSocketListener.stocksData

    fun subscribe(symbols: List<String>) {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val jsonAdapter = moshi.adapter(StockSubRequest::class.java)

        symbols.forEach { symbol ->
            val msg = jsonAdapter.toJson(StockSubRequest("subscribe", symbol))
            webSocket.send(msg)
        }
    }

    fun close() {
        webSocket.close(1000, "End")
    }
}