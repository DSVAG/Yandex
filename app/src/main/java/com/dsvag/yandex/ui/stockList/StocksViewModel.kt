package com.dsvag.yandex.ui.stockList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsvag.yandex.data.repositories.StocksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StocksViewModel @Inject constructor(
    private val stocksRepository: StocksRepository,
) : ViewModel() {

    val defaultStockFlow get() = stocksRepository.defaultStockFlow

    val favoriteStockFlow get() = stocksRepository.favoriteStockFlow

    fun subscribe() {
        viewModelScope.launch {
            stocksRepository.subscribe()
        }
    }

    fun changeFavoriteStatus(ticker: String, isFavorite: Boolean) {
        viewModelScope.launch {
            if (isFavorite) {
                stocksRepository.addToFavorite(ticker)
            } else {
                stocksRepository.removeFromFavorite(ticker)
            }
        }
    }

}