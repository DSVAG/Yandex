package com.dsvag.yandex.data.repositores

import com.dsvag.yandex.data.local.StockDao
import com.dsvag.yandex.data.remote.FinnhubApi
import com.dsvag.yandex.models.Stock
import com.dsvag.yandex.models.StockData
import com.dsvag.yandex.models.StockDataResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import okhttp3.*
import javax.inject.Inject
import kotlin.math.absoluteValue

class StockRepository @Inject constructor(
    private val finnhubApi: FinnhubApi,
    private val stockDao: StockDao,
    private val okHttpClient: OkHttpClient,
    private val request: Request,
) {
    val defaultStockFlow = stockDao.getDefaultStocks()

    val favoriteStockFlow = stockDao.getFavoriteStock()

    private val moshi by lazy { Moshi.Builder().add(KotlinJsonAdapterFactory()).build() }
    private val jsonAdapter by lazy { moshi.adapter(StockDataResponse::class.java) }

    private suspend fun observeStockChanges(tickers: List<String>): Flow<List<StockData>> {
        return callbackFlow {
            val webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {

                override fun onMessage(webSocket: WebSocket, text: String) {
                    jsonAdapter.fromJson(text)?.let { stockDataResponse ->
                        if (stockDataResponse.type == "trade") {
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
                val msg = "{\"type\":\"subscribe\",\"symbol\":\"$ticker\"}"
                webSocket.send(msg)
            }

            awaitClose { webSocket.close(1000, null) }
        }
    }

    suspend fun subscribe() {
        defaultTickers.addAll(stockDao.getFavoriteTicker())

        defaultTickers.forEach { ticker ->
            val stock = stockDao.getStock(ticker)
            val stockPrice = finnhubApi.fetchCurrentPrice(ticker)

            if (stock == null) {
                val stockInfo = finnhubApi.fetchStockInfo(ticker)

                stockDao.insert(
                    Stock(
                        stockInfo.company,
                        stockInfo.ticker,
                        stockPrice.c,
                        0.0,
                        0.0,
                        isDefault = true,
                    )
                )
            } else {
                stockDao.update(stock.updatePrice(stockPrice.c))
            }
        }

        observeStockChanges(defaultTickers.toList()).collect { stockDataList ->
            stockDataList.forEach { (newStockPrice, ticker) ->
                stockDao.getStock(ticker)?.let { stock ->

                    stockDao.update(stock.updatePrice(newStockPrice))
                }
            }
        }
    }

    private fun Stock.updatePrice(newPrice: Double): Stock {
        val priceChange = this.price - newPrice
        val priceChangePercent = ((newPrice - this.price) / this.price).absoluteValue

        return this.copy(price = newPrice, priceChange = priceChange, priceChangePercent = priceChangePercent)
    }

    companion object {
        private val defaultTickers = mutableSetOf(
            "YNDX", "MSFT", "AMZN", "FB", "AAPL", "JPM", "NFLX", "JNJ", "TSLA", "XOM", "IBM", "BAC", "WFC", "INTC",
            "T", "V", "CVX", "UNH", "PFE", "HD", "PG", "VZ", "NVDA", "WMT",
        )
    }
}