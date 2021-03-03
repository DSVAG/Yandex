package com.dsvag.yandex.data.repositoyes

import com.dsvag.yandex.data.local.StockDao
import com.dsvag.yandex.data.remote.ApiFinnhubService
import com.dsvag.yandex.data.remote.StockWebSocketListener
import com.dsvag.yandex.models.Stock
import com.dsvag.yandex.models.StockSubscribeRequest
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.combine
import okhttp3.WebSocket
import javax.inject.Inject

class StockRepository @Inject constructor(
    private val stockWebSocketListener: StockWebSocketListener,
    private val webSocket: WebSocket,
    private val apiFinnhubService: ApiFinnhubService,
    private val stockDao: StockDao,
) {
    private val _stockFlow = stockDao.getStockInfoByTicker(tickerList)
    private val _stockDataFlow = stockWebSocketListener.stocksData

    val stockFlow = _stockFlow.combine(_stockDataFlow) { stockList, stockDataMap ->
        stockList.map { stock ->
            val stockData = stockDataMap[stock.ticker]

            stock.copy(
                lastPrice = stockData?.lastPrice ?: stock.lastPrice,
                volume = stockData?.volume ?: stock.volume
            )
        }
    }

    suspend fun subscribe() {
        tickerList.forEach { ticker ->
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

        tickerList.forEach { symbol ->
            val msg = jsonAdapter.toJson(StockSubscribeRequest("subscribe", symbol))
            webSocket.send(msg)
        }
    }

    fun close() {
        webSocket.close(1000, "End")
    }

    suspend fun addToFavorite(stock: Stock) {
        stockDao.insert(stock.copy(isFavorite = true))
    }

    companion object {
        private val tickerList = listOf(
            "YNDX", "AAPL", "MSFT", "AMZN", "FB", "JPM", "NFLX", "JNJ", "GOOGL", "XOM", "BAC", "WFC", "INTC",
            "T", "V", "CVX", "UNH", "PFE", "HD", "PG", "DIS", "VZ", "NVDA", "WMT",
        )
    }
}