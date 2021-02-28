package com.dsvag.yandex.ui.stockList

import androidx.lifecycle.ViewModel
import com.dsvag.yandex.data.repositoyes.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StocksViewModel @Inject constructor(
    private val stockRepository: StockRepository,
) : ViewModel() {

    fun sub() {
        stockRepository.subscribe(tickerList)
    }

    companion object {
        private val tickerList = listOf(
            "YNDX", "AAPL", "MSFT", "AMZN", "FB", "JPM", "BRK.B", "JNJ", "GOOGL", "XOM", "BAC", "WFC", "INTC",
            "T", "V", "CSCO", "CVX", "UNH", "PFE", "HD", "PG", "VZ", "NVDA", "MMM",
        )
    }
}