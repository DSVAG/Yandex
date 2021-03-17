package com.dsvag.yandex.data.remote

import com.dsvag.yandex.models.StockDataResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class StockWebSocketListener : WebSocketListener() {

    val stockMap = mutableMapOf<String, Double>()

    private val moshi by lazy { Moshi.Builder().add(KotlinJsonAdapterFactory()).build() }
    private val jsonAdapter by lazy { moshi.adapter(StockDataResponse::class.java) }

    override fun onMessage(webSocket: WebSocket, text: String) {
        jsonAdapter.fromJson(text)?.let { stockDataResponse ->
            if (stockDataResponse.type == "trade") {

                stockDataResponse.stockData.forEach { stockData ->
                    stockMap[stockData.ticker] = stockData.lastPrice
                }
            }
        }
    }
}