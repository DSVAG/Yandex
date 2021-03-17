package com.dsvag.yandex.data.repositoyes

import com.dsvag.yandex.data.local.StockDao
import com.dsvag.yandex.data.remote.FinnhubApi
import com.dsvag.yandex.data.remote.StockWebSocketListener
import com.dsvag.yandex.models.Stock
import com.dsvag.yandex.models.StockInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.WebSocket
import javax.inject.Inject

class StockRepository @Inject constructor(
    private val stockWebSocketListener: StockWebSocketListener,
    private val webSocket: WebSocket,
    private val finnhubApi: FinnhubApi,
    private val stockDao: StockDao,
) {

    private val _stockDataFlow = flow {
        while (true) {
            emit(stockWebSocketListener.stockMap)
            delay(5000)
        }
    }.flowOn(Dispatchers.IO)

    private val _stockFlow = stockDao.getDefaultStocks()

    val stockFlow = _stockDataFlow.combine(_stockFlow) { stocksData, stocks ->
        stocks.map { stock ->
            val newStockPrice = stocksData[stock.ticker] ?: stock.price
            val priceChange = stock.price - newStockPrice
            val priceChangePercent = (newStockPrice - stock.price) / stock.price

            stock.copy(price = newStockPrice, priceChange = priceChange, priceChangePercent = priceChangePercent)
        }
    }

    suspend fun subscribe() {
        defaultTickers.forEach { ticker ->
            if (stockDao.getStock(ticker) == null) {
                val stockInfo = finnhubApi.fetchStockInfo(ticker)
                stockDao.insert(stockInfo.toStock())
            }
        }

        defaultTickers.forEach { symbol ->
            val msg = "{\"type\":\"subscribe\",\"symbol\":\"$symbol\"}"
            webSocket.send(msg)
        }
    }

    fun unSubscribe() {
        webSocket.close(1000, "End")
    }


    private fun StockInfo.toStock() = Stock(
        this.logo, this.company, this.ticker, 0.0, 0.0, 0.0, false
    )

    companion object {
        private val defaultTickers = listOf(
            "YNDX", "AAPL", "MSFT", "AMZN", "FB", "JPM", "NFLX", "JNJ", "GOOGL", "XOM", "IBM", "BAC", "WFC", "INTC",
            "T", "V", "CVX", "UNH", "PFE", "HD", "PG", "VZ", "NVDA", "WMT",
        )
    }
}