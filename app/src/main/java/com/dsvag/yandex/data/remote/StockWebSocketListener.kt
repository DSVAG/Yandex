package com.dsvag.yandex.data.remote

import com.dsvag.yandex.models.StockData
import com.dsvag.yandex.models.StockDataResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.runBlocking
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class StockWebSocketListener : WebSocketListener() {

    private val stockMap = mutableMapOf<String, StockData>()
    private val _stocksData = MutableSharedFlow<Map<String, StockData>>()
    val stocksData: SharedFlow<Map<String, StockData>> get() = _stocksData

    private val moshi by lazy { Moshi.Builder().add(KotlinJsonAdapterFactory()).build() }
    private val jsonAdapter by lazy { moshi.adapter(StockDataResponse::class.java) }

    override fun onMessage(webSocket: WebSocket, text: String) {
        jsonAdapter.fromJson(text)?.let { stockDataResponse ->
            if (stockDataResponse.type == "trade") {
                runBlocking {
                    stockDataResponse.stockData.forEach {
                        stockMap[it.ticker] = it
                    }
                    _stocksData.emit(stockMap.toMap())
                }
            }
        }
    }
}