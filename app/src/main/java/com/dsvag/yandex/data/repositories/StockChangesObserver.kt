package com.dsvag.yandex.data.repositories

import com.dsvag.yandex.models.finnhub.SocketMsg
import com.dsvag.yandex.models.finnhub.StockData
import com.dsvag.yandex.models.finnhub.StockDataResponse
import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.*

class StockChangesObserver(
    private val okHttpClient: OkHttpClient,
    private val request: Request,
    private val stockDataResponseJsonAdapter: JsonAdapter<StockDataResponse>,
    private val socketMsgJsonAdapter: JsonAdapter<SocketMsg>,
) {

    private var webSocket: WebSocket? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun observeStockChanges(tickers: List<String>): Flow<List<StockData>> {
        return callbackFlow {
            webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {

                override fun onMessage(webSocket: WebSocket, text: String) {
                    stockDataResponseJsonAdapter.fromJson(text)?.let { stockDataResponse ->
                        if (stockDataResponse.type == TYPE_TRADE) {
                            sendBlocking(stockDataResponse.stockData)
                        }
                    }
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    cancel(CancellationException(null, null))
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    cancel(CancellationException(null, t))
                }
            })

            tickers.forEach { ticker ->
                subscribeToTicker(ticker)
            }

            awaitClose { webSocket?.close(1000, null) }
        }
    }

    fun subscribeToTicker(ticker: String) {
        webSocket?.send(generateMsg(SocketMsg(SUBSCRIBE, ticker)))
    }

    fun unsubscribeFromTicker(ticker: String) {
        webSocket?.send(generateMsg(SocketMsg(UNSUBSCRIBE, ticker)))
    }

    private fun generateMsg(msg: SocketMsg): String {
        return socketMsgJsonAdapter.toJson(msg)
    }

    private companion object {
        const val SUBSCRIBE = "subscribe"
        const val UNSUBSCRIBE = "unsubscribe"
        const val TYPE_TRADE = "trade"
    }
}
