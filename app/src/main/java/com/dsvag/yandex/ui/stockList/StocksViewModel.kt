package com.dsvag.yandex.ui.stockList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsvag.yandex.data.repositoyes.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StocksViewModel @Inject constructor(
    private val stockRepository: StockRepository,
) : ViewModel() {

    val stockFlow get() = stockRepository.stockFlow

    fun sub() {
        viewModelScope.launch {
            stockRepository.subscribe()
        }
    }

}