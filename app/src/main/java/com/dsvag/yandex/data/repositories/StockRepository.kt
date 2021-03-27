package com.dsvag.yandex.data.repositories

import com.dsvag.yandex.base.isNull
import com.dsvag.yandex.data.local.StockDao
import com.dsvag.yandex.data.remote.FinnhubApi
import com.dsvag.yandex.data.remote.YandexApi
import com.dsvag.yandex.models.Stock
import com.dsvag.yandex.models.finnhub.StockData
import com.dsvag.yandex.models.finnhub.StockDataResponse
import com.dsvag.yandex.models.yandex.search.SearchApiRequest
import com.dsvag.yandex.models.yandex.stock.StockApiRequest
import com.dsvag.yandex.models.yandex.stock.response.StockResponse
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
    private val yandexApi: YandexApi,
    private val stockDao: StockDao,
    private val okHttpClient: OkHttpClient,
    private val request: Request,
) {
    val defaultStockFlow = stockDao.getDefaultStocks()

    val favoriteStockFlow = stockDao.getFavoriteStock()

    private val moshi by lazy { Moshi.Builder().add(KotlinJsonAdapterFactory()).build() }

    private var webSocket: WebSocket? = null

    private var token = "test"

    private suspend fun observeStockChanges(tickers: List<String>): Flow<List<StockData>> {
        return callbackFlow {
            webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {

                private val stockDataJsonAdapter by lazy { moshi.adapter(StockDataResponse::class.java) }

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

    suspend fun subscribe() {
        token = yandexApi.getToken().token

        defaultTickers.addAll(stockDao.getFavoriteTickers())

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

    suspend fun search(searchRequest: SearchApiRequest): List<Stock> {
        val response = yandexApi.search(token, searchRequest)

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

    suspend fun fetchStock(stockRequest: StockApiRequest): StockResponse {
        return yandexApi.fetchStockInfo(token, stockRequest)
    }

    private fun Stock.updatePrice(newPrice: Double): Stock {
        val priceChange = this.price - newPrice
        val priceChangePercent = ((newPrice - this.price) / this.price).absoluteValue

        return this.copy(price = newPrice, priceChange = priceChange, priceChangePercent = priceChangePercent)
    }

    private fun generateMsg(command: String, ticker: String): String {
        return "{\"type\":\"$command\",\"symbol\":\"$ticker\"}"
    }

    companion object {
        private val defaultTickers = mutableSetOf(
            "YNDX", "MSFT", "AMZN", "FB", "AAPL", "JPM", "NFLX", "JNJ", "TSLA", "XOM", "IBM", "BAC", "WFC", "INTC", "T",
            "V", "CVX", "UNH", "PFE", "HD", "PG", "VZ", "NVDA", "WMT",
        )
    }
}