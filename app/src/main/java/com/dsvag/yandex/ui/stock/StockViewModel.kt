package com.dsvag.yandex.ui.stock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsvag.yandex.data.repositories.StockRepository
import com.dsvag.yandex.models.Stock
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockViewModel @Inject constructor(
    private val stocksRepository: StockRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<State>(State.Default)
    val state: StateFlow<State> get() = _state

    fun fetchStock(ticker: String) {

        viewModelScope.launch {
            stocksRepository.fetchStock(ticker)

        }
    }

    sealed class State {
        object Default : State()
        object Loading : State()

        data class Success(val stocks: List<Stock>) : State()
    }
}