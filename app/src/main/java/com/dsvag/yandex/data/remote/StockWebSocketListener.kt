package com.dsvag.yandex.data.remote

import android.util.Log
import com.dsvag.yandex.models.StockData
import com.dsvag.yandex.models.StockDataResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class StockWebSocketListener : WebSocketListener() {

    private val _stockDataflow = MutableSharedFlow<List<StockData>>()
    val stockDataflow = _stockDataflow.asSharedFlow()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val moshi by lazy { Moshi.Builder().add(KotlinJsonAdapterFactory()).build() }
    private val jsonAdapter by lazy { moshi.adapter(StockDataResponse::class.java) }

    override fun onMessage(webSocket: WebSocket, text: String) {
        coroutineScope.launch {
            Log.d("kek", text)

            jsonAdapter.fromJson(text)?.let { stockDataResponse ->
                if (stockDataResponse.type == "trade") {
                    val msg = _stockDataflow.emit(stockDataResponse.stockData)
                    Log.d("kek", "emit = $msg")
                }
            }
        }
    }
}