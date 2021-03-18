package com.dsvag.yandex.ui.stockList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsvag.yandex.data.repositoyes.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
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

    fun unSubscribe() {
        viewModelScope.launch {
            stockRepository.unSubscribe()
        }
    }

}