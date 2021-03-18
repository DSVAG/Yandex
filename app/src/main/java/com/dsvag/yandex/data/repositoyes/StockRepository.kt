package com.dsvag.yandex.data.repositoyes

import android.util.Log
import com.dsvag.yandex.data.local.StockDao
import com.dsvag.yandex.data.remote.FinnhubApi
import com.dsvag.yandex.data.remote.StockWebSocketListener
import com.dsvag.yandex.models.Stock
import com.dsvag.yandex.models.StockInfo
import kotlinx.coroutines.flow.collect
import okhttp3.WebSocket
import javax.inject.Inject

class StockRepository @Inject constructor(
    private val stockWebSocketListener: StockWebSocketListener,
    private val webSocket: WebSocket,
    private val finnhubApi: FinnhubApi,
    private val stockDao: StockDao,
) {
    val defaultStockFlow = stockDao.getDefaultStocks()

    val favoriteStockFlow = stockDao.getFavoriteStock()

    suspend fun subscribe() {
        defaultTickers.addAll(stockDao.getStocksTicker())

        defaultTickers.forEach { ticker ->
            if (stockDao.getStock(ticker) == null) {
                val stockInfo = finnhubApi.fetchStockInfo(ticker)
                stockDao.insert(stockInfo.toStock().copy(isDefault = true))
            }
        }

        defaultTickers.forEach { symbol ->
            val msg = "{\"type\":\"subscribe\",\"symbol\":\"$symbol\"}"
            webSocket.send(msg)
        }

        stockWebSocketListener.stockDataflow.collect { stockData ->
            Log.d("kek", "flow =" + stockData.joinToString())
            stockData.forEach { (newStockPrice, ticker) ->
                var stock = stockDao.getStock(ticker)

                if (stock != null) {
                    val priceChange = stock.price - newStockPrice
                    val priceChangePercent = (newStockPrice - stock.price) / stock.price

                    stock = stock.copy(
                        price = newStockPrice,
                        priceChange = priceChange,
                        priceChangePercent = priceChangePercent,
                    )

                    stockDao.insert(stock)
                }
            }
        }
    }

    fun unSubscribe() {
        webSocket.close(1000, "End")
    }

    private fun StockInfo.toStock() = Stock(this.company, this.ticker, 0.0, 0.0, 0.0)

    companion object {
        private val defaultTickers = mutableListOf(
            "YNDX", "AAPL", "MSFT", "AMZN", "FB", "JPM", "NFLX", "JNJ", "TSLA", "XOM", "IBM", "BAC", "WFC", "INTC",
            "T", "V", "CVX", "UNH", "PFE", "HD", "PG", "VZ", "NVDA", "WMT",
        )
    }
}