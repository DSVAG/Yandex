package com.dsvag.yandex.ui.stockDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsvag.yandex.data.repositoyes.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockViewModel @Inject constructor(
    private val stockRepository: StockRepository,
) : ViewModel() {

    fun fetchStock(ticker: String) {
        viewModelScope.launch {

        }
    }


}