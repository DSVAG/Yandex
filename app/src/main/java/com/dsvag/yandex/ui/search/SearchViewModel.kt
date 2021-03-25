package com.dsvag.yandex.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsvag.yandex.data.repositories.StockRepository
import com.dsvag.yandex.models.Stock
import com.dsvag.yandex.models.yandex.SearchRequest
import com.dsvag.yandex.models.yandex.Variables
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val stockRepository: StockRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<State>(State.Empty)
    val state: StateFlow<State> get() = _state

    fun search(query: String) {
        if (query.isEmpty()) {
            _state.value = State.Empty
        } else {
            _state.value = State.Loading
            val variables = Variables(searchText = query)
            val searchRequest = SearchRequest(variables = variables)

            viewModelScope.launch {
                val response = stockRepository.search(searchRequest)
                _state.value = State.Success(response)
            }
        }
    }

    fun changeFavoriteStatus(stock: Stock, isFavorite: Boolean) {
        viewModelScope.launch {
            if (isFavorite) {
                stockRepository.addToFavorite(stock)
            } else {
                stockRepository.removeFromFavorite(stock)
            }
        }
    }

    sealed class State {
        object Empty : State()
        object Loading : State()

        data class Success(val stocks: List<Stock>) : State()
        data class Error(val msg: String) : State()
    }
}