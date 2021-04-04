package com.dsvag.yandex.data.repositories

import com.dsvag.yandex.data.local.StockDao
import com.dsvag.yandex.data.remote.YandexApi
import com.dsvag.yandex.models.Stock
import com.dsvag.yandex.models.yandex.chart.ChartRequest
import com.dsvag.yandex.models.yandex.chart.response.ChartResponse
import com.dsvag.yandex.models.yandex.search.SearchRequest
import com.dsvag.yandex.models.yandex.stock.StockRequest
import com.dsvag.yandex.models.yandex.stock.StockVariables
import com.dsvag.yandex.models.yandex.stock.response.StockResponse
import com.dsvag.yandex.models.yandex.stockPrice.StockPriceRequest
import com.dsvag.yandex.models.yandex.stockPrice.StockPriceVariables
import kotlinx.coroutines.flow.collect
import kotlin.math.absoluteValue

class StockRepository(
    private val yandexApi: YandexApi,
    private val stockDao: StockDao,
    private val stockChangesObserver: StockChangesObserver,
) {
    val defaultStockFlow = stockDao.getDefaultStocks()

    val favoriteStockFlow = stockDao.getFavoriteStock()

    suspend fun subscribe() {
        token = yandexApi.getToken().token

        val tickers = stockDao.getTickers().toMutableSet().apply { addAll(TICKER_DEFAULT) }

        tickers.forEach { ticker ->
            val stock = stockDao.getStock(ticker)

            if (stock == null) {
                val variables = StockVariables(slug = ticker)
                val apiRequest = StockRequest(variables = variables)
                val stockInfo = yandexApi.fetchStockInfo(token, apiRequest).data.instruments.metaData

                stockDao.insert(
                    Stock(
                        stockInfo.id,
                        stockInfo.displayName,
                        stockInfo.logoId,
                        ticker,
                        stockInfo.marketData.price,
                        stockInfo.marketData.absoluteChange,
                        stockInfo.marketData.percentChange,
                        isDefault = true,
                    )
                )
            } else {
                val variables = StockPriceVariables(slug = ticker)
                val apiRequest = StockPriceRequest(stockPriceVariables = variables)
                val stockPrice = yandexApi.fetchStockPrice(token, apiRequest)

                stockDao.update(
                    stock.copy(
                        price = stockPrice.data.instruments.metaData.marketData.price,
                        priceChange = stockPrice.data.instruments.metaData.marketData.absoluteChange,
                        priceChangePercent = stockPrice.data.instruments.metaData.marketData.percentChange,
                    )
                )
            }
        }

        stockChangesObserver.observeStockChanges(TICKER_DEFAULT.toList()).collect { stockDataList ->
            stockDataList.forEach { (newStockPrice, ticker) ->
                stockDao.getStock(ticker)?.let { stock ->
                    stockDao.update(stock.updatePrice(newStockPrice))
                }
            }
        }
    }

    suspend fun addToFavorite(stock: Stock) {
        if (stockDao.getStock(stock.ticker) == null) {
            stockDao.insert(stock)
            stockChangesObserver.subscribeToTicker(stock.ticker)
        } else {
            stockDao.update(stock.copy(isFavorite = true))
        }
    }

    suspend fun removeFromFavorite(stock: Stock) {
        if (stock.isDefault) {
            stockDao.update(stock)
        } else {
            stockChangesObserver.unsubscribeFromTicker(stock.ticker)
            stockDao.delete(stock)
        }
    }

    suspend fun search(searchRequest: SearchRequest): List<Stock> {
        val response = yandexApi.search(token, searchRequest)

        return response.info.instruments.catalog.results.map { result ->
            Stock(
                id = result.id,
                company = result.displayName,
                logo = result.logoId,
                ticker = result.ticker,
                price = result.marketData.price,
                priceChange = result.marketData.absoluteChange ?: 0.0,
                priceChangePercent = result.marketData.percentChange ?: 0.0,
            )
        }
    }

    suspend fun fetchStock(stockRequest: StockRequest): StockResponse {
        return yandexApi.fetchStockInfo(token, stockRequest)
    }

    suspend fun fetchStockChart(chartRequest: ChartRequest): ChartResponse {
        return yandexApi.fetchStockChart(token, chartRequest)
    }

    private fun Stock.updatePrice(newPrice: Double): Stock {
        val priceChange = this.price - newPrice
        val priceChangePercent = ((newPrice - this.price) / this.price).absoluteValue

        return this.copy(price = newPrice, priceChange = priceChange, priceChangePercent = priceChangePercent)
    }

    private companion object {
        val TICKER_DEFAULT = setOf(
            "YNDX", "MSFT", "AMZN", "FB", "AAPL", "JPM", "NFLX", "JNJ", "TSLA", "XOM", "IBM", "BAC", "WFC", "INTC", "T",
            "V", "CVX", "UNH", "PFE", "HD", "PG", "VZ", "NVDA", "WMT",
        )

        var token = "token"
    }
}