package com.dsvag.yandex.ui.search

import androidx.lifecycle.ViewModel
import com.dsvag.yandex.models.Stock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class StockSearchViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow<State>(State.Empty)
    val state: StateFlow<State> get() = _state

    fun search(query: String) {

    }


    sealed class State {
        object Empty : State()
        object Loading : State()

        data class Result(val stocks: List<Stock>) : State()
    }
}