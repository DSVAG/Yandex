package com.dsvag.yandex.data.repositories

import com.dsvag.yandex.data.local.StockDao
import com.dsvag.yandex.data.remote.YandexApi
import com.dsvag.yandex.models.Stock
import com.dsvag.yandex.models.yandex.chart.ChartRequest
import com.dsvag.yandex.models.yandex.search.SearchRequest
import com.dsvag.yandex.models.yandex.stock.StockRequest
import com.dsvag.yandex.models.yandex.stock.StockVariables
import com.dsvag.yandex.models.yandex.stock.response.StockResponse
import com.dsvag.yandex.models.yandex.stockPrice.StockPriceRequest
import com.dsvag.yandex.models.yandex.stockPrice.StockPriceVariables
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.sample
import java.math.BigDecimal

class StockRepository(
    private val yandexApi: YandexApi,
    private val stockDao: StockDao,
    private val stockChangesObserver: StockChangesObserver,
) {
    val defaultStockFlow = stockDao.getDefaultStocks().sample(1000)

    val favoriteStockFlow = stockDao.getFavoriteStock().sample(1000)

    suspend fun subscribe() {
        token = yandexApi.getToken().token
        fetchUsd()

        val tickers = stockDao.getTickers().toMutableSet().apply { addAll(TICKER_DEFAULT) }

        tickers.forEach { ticker ->
            val stock = stockDao.getStock(ticker)
            fetchUsd()

            if (stock == null) {
                val variables = StockVariables(slug = ticker)
                val apiRequest = StockRequest(variables = variables)
                val stockInfo = yandexApi.fetchStockInfo(token, apiRequest).data.instruments.metaData

                if (stockInfo.marketData.currencyCode == CURRENCY_CODE_RUB) {
                    stockDao.insert(
                        Stock(
                            stockInfo.id,
                            stockInfo.displayName,
                            stockInfo.logoId,
                            ticker,
                            BigDecimal(stockInfo.marketData.price).divide(usd, 5),
                            BigDecimal(stockInfo.marketData.absoluteChange).divide(usd, 5),
                            BigDecimal(stockInfo.marketData.percentChange),
                            isDefault = true,
                        )
                    )

                } else {
                    stockDao.insert(
                        Stock(
                            stockInfo.id,
                            stockInfo.displayName,
                            stockInfo.logoId,
                            ticker,
                            BigDecimal(stockInfo.marketData.price),
                            BigDecimal(stockInfo.marketData.absoluteChange),
                            BigDecimal(stockInfo.marketData.percentChange),
                            isDefault = true,
                        )
                    )
                }
            } else {
                val variables = StockPriceVariables(slug = ticker)
                val apiRequest = StockPriceRequest(stockPriceVariables = variables)
                val stockPrice = yandexApi.fetchStockPrice(token, apiRequest)

                if (stockPrice.data.instruments.metaData.marketData.currencyCode == CURRENCY_CODE_RUB) {
                    stock.copy(
                        price = BigDecimal(stockPrice.data.instruments.metaData.marketData.price).divide(usd, 5),
                        priceChange = BigDecimal(stockPrice.data.instruments.metaData.marketData.absoluteChange).divide(usd, 5),
                        priceChangePercent = BigDecimal(stockPrice.data.instruments.metaData.marketData.percentChange),
                    )
                } else {
                    stockDao.update(
                        stock.copy(
                            price = BigDecimal(stockPrice.data.instruments.metaData.marketData.price),
                            priceChange = BigDecimal(stockPrice.data.instruments.metaData.marketData.absoluteChange),
                            priceChangePercent = BigDecimal(stockPrice.data.instruments.metaData.marketData.percentChange),
                        )
                    )
                }
            }
        }

        stockChangesObserver.observeStockChanges(TICKER_DEFAULT.toList()).collect { stockDataList ->
            stockDataList.forEach { (newStockPrice, ticker) ->
                stockDao.getStock(ticker)?.let { stock ->
                    stockDao.update(stock.updatePrice(BigDecimal(newStockPrice)))
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
            if (result.marketData.currencyCode == CURRENCY_CODE_RUB) {
                Stock(
                    result.id,
                    result.displayName,
                    result.logoId,
                    result.ticker,
                    BigDecimal(result.marketData.price).divide(usd, 5),
                    BigDecimal(result.marketData.absoluteChange ?: 0.0).divide(usd, 5),
                    BigDecimal(result.marketData.percentChange ?: 0.0),
                )
            } else {
                Stock(
                    result.id,
                    result.displayName,
                    result.logoId,
                    result.ticker,
                    BigDecimal(result.marketData.price),
                    BigDecimal(result.marketData.absoluteChange ?: 0.0),
                    BigDecimal(result.marketData.percentChange ?: 0.0),
                )
            }
        }
    }

    suspend fun fetchStock(stockRequest: StockRequest): StockResponse {
        return yandexApi.fetchStockInfo(token, stockRequest)
    }

    suspend fun fetchStockChart(chartRequest: ChartRequest, currencyCode: String): List<Pair<Long, BigDecimal>> {
        return yandexApi.fetchStockChart(token, chartRequest).data.candles.seriesBefore
            .map { (date, price) ->
                if (currencyCode == CURRENCY_CODE_RUB) Pair(date, price.divide(usd, 5))
                else Pair(date, price)
            }
    }

    private fun Stock.updatePrice(newPrice: BigDecimal): Stock {
        val priceChange = this.price.minus(newPrice)
        val priceChangePercent = newPrice.minus(this.price).divide(this.price, 5).abs()

        return this.copy(price = newPrice, priceChange = priceChange, priceChangePercent = priceChangePercent)
    }

    private suspend fun fetchUsd() {
        val tmpUsd =
            yandexApi.fetchStockPrice(token, StockPriceRequest(stockPriceVariables = StockPriceVariables(slug = "usd")))

        usd = BigDecimal(tmpUsd.data.instruments.metaData.marketData.price)
    }

    private companion object {
        val TICKER_DEFAULT = setOf(
            "YNDX", "MSFT", "AMZN", "FB", "AAPL", "JPM", "NFLX", "JNJ", "TSLA", "XOM", "IBM", "BAC", "WFC", "INTC", "T",
            "V", "CVX", "UNH", "PFE", "HD", "PG", "VZ", "NVDA", "WMT",
        )

        var token = "token"
        var usd: BigDecimal = BigDecimal.ZERO
        var CURRENCY_CODE_RUB = "RUB"
    }
}