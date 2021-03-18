package com.dsvag.yandex.ui.stockList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsvag.yandex.data.repositores.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StocksViewModel @Inject constructor(
    private val stockRepository: StockRepository,
) : ViewModel() {

    val defaultStockFlow get() = stockRepository.defaultStockFlow

    val favoriteStockFlow get() = stockRepository.favoriteStockFlow

    fun subscribe() {
        viewModelScope.launch {
            stockRepository.subscribe()
        }
    }

    fun changeFavoriteStatus(ticker: String, isFavorite: Boolean) {
        viewModelScope.launch {
            if (isFavorite) {
                stockRepository.addToFavorite(ticker)
            } else {
                stockRepository.removeFromFavorite(ticker)
            }
        }
    }

}