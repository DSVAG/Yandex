package com.dsvag.yandex.data.remote

import android.util.Log
import com.dsvag.yandex.models.StockData
import com.dsvag.yandex.models.StockDataResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class StockWebSocketListener : WebSocketListener() {

    private val _stocksData = MutableStateFlow<MutableList<StockData>>(mutableListOf())
    val stocksData: StateFlow<List<StockData>> get() = _stocksData

    private val moshi by lazy { Moshi.Builder().add(KotlinJsonAdapterFactory()).build() }

    private val jsonAdapter by lazy { moshi.adapter(StockDataResponse::class.java) }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d(TAG, "Open")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d(TAG, "Message: $text")

        jsonAdapter.fromJson(text)?.stockData?.let {
            _stocksData.value.add(it.last())
        }
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e(TAG, "Failure", t)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.d(TAG, "Closing: $reason")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Log.d(TAG, "Closed: $reason")
    }

    private companion object {
        const val TAG = "StockWebSocketListener"
    }
}