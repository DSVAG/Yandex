package com.dsvag.yandex.data.repositoyes

import com.dsvag.yandex.data.local.StockDao
import com.dsvag.yandex.data.remote.ApiFinnhubService
import com.dsvag.yandex.data.remote.StockWebSocketListener
import com.dsvag.yandex.models.Stock
import com.dsvag.yandex.models.StockSubscribeRequest
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.WebSocket
import javax.inject.Inject

class StockRepository @Inject constructor(
    private val stockWebSocketListener: StockWebSocketListener,
    private val webSocket: WebSocket,
    private val apiFinnhubService: ApiFinnhubService,
    private val stockDao: StockDao,
) {

    val stockFlow get() = stockDao.getDefaultStocks()

    suspend fun subscribe() {
        defaultTickers.forEach { ticker ->
            if (stockDao.getStock(ticker) == null) {
                val stockInfo = apiFinnhubService.fetchStockInfo(ticker)
                stockDao.insert(
                    Stock(
                        stockInfo.logo,
                        stockInfo.company,
                        stockInfo.ticker,
                        stockInfo.webUrl,
                        0.0,
                        0.0,
                        false
                    )
                )
            }
        }

        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val jsonAdapter = moshi.adapter(StockSubscribeRequest::class.java)

        defaultTickers.forEach { symbol ->
            val msg = jsonAdapter.toJson(StockSubscribeRequest("subscribe", symbol))
            // webSocket.send(msg)
        }
    }

    fun unSubscribe() {
        webSocket.close(1000, "End")
    }

    companion object {
        private val defaultTickers = listOf(
            "YNDX", "AAPL", "MSFT", "AMZN", "FB", "JPM", "NFLX", "JNJ", "GOOGL", "XOM", "IBM", "BAC", "WFC", "INTC",
            "T", "V", "CVX", "UNH", "PFE", "HD", "PG", "VZ", "NVDA", "WMT",
        )
    }
}