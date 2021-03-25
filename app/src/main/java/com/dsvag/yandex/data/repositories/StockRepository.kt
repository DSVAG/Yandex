package com.dsvag.yandex.data.repositories

import com.dsvag.yandex.base.isNull
import com.dsvag.yandex.data.local.StockDao
import com.dsvag.yandex.data.remote.FinnhubApi
import com.dsvag.yandex.data.remote.YandexApi
import com.dsvag.yandex.models.Stock
import com.dsvag.yandex.models.finnhub.StockData
import com.dsvag.yandex.models.finnhub.StockDataResponse
import com.dsvag.yandex.models.yandex.SearchRequest
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    private val yandexApi: YandexApi,
    private val stockDao: StockDao,
    private val okHttpClient: OkHttpClient,
    private val request: Request,
) {
    val defaultStockFlow = stockDao.getDefaultStocks()

    val favoriteStockFlow = stockDao.getFavoriteStock()

    private val moshi by lazy { Moshi.Builder().add(KotlinJsonAdapterFactory()).build() }
    private val stockDataJsonAdapter by lazy { moshi.adapter(StockDataResponse::class.java) }

    private var webSocket: WebSocket? = null

    @ExperimentalCoroutinesApi
    private suspend fun observeStockChanges(tickers: List<String>): Flow<List<StockData>> {
        return callbackFlow {
            webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {

                override fun onMessage(webSocket: WebSocket, text: String) {
                    stockDataJsonAdapter.fromJson(text)?.let { stockDataResponse ->
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
                webSocket?.send(generateMsg("subscribe", ticker))
            }

            awaitClose { webSocket?.close(1000, null) }
        }
    }

    @ExperimentalCoroutinesApi
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

    suspend fun addToFavorite(stock: Stock) {
        defaultTickers.add(stock.ticker)

        if (stockDao.getStock(stock.ticker).isNull()) {
            stockDao.insert(stock.copy(isFavorite = true))
            webSocket?.send(generateMsg("subscribe", stock.ticker))
        } else {
            stockDao.update(stock.copy(isFavorite = true))
        }
    }

    suspend fun removeFromFavorite(stock: Stock) {
        if (stock.isDefault) {
            stockDao.update(stock.copy(isFavorite = false))
        } else {
            webSocket?.send(generateMsg("unsubscribe", stock.ticker))
            defaultTickers.remove(stock.ticker)
            stockDao.delete(stock)
        }
    }

    suspend fun search(searchRequest: SearchRequest): List<Stock> {
        val response = yandexApi.search(searchRequest)

        return response.info.instruments.catalog.results.map { result ->
            Stock(
                company = result.displayName,
                logo = result.logoId,
                ticker = result.ticker,
                price = result.marketData.price,
                priceChange = result.marketData.absoluteChange,
                priceChangePercent = result.marketData.percentChange
            )
        }
    }

    suspend fun fetchStock(ticker: String) {

    }

    private fun generateMsg(command: String, ticker: String): String {
        return "{\"type\":\"$command\",\"symbol\":\"$ticker\"}"
    }

    companion object {
        private val defaultTickers = mutableSetOf(
            "YNDX", "MSFT", "AMZN", "FB", "AAPL", "JPM", "NFLX", "JNJ", "TSLA", "XOM", "IBM", "BAC", "WFC",
            "INTC", "T", "V", "CVX", "UNH", "PFE", "HD", "PG", "VZ", "NVDA", "WMT",
        )
    }
}