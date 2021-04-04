package com.dsvag.yandex.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsvag.yandex.data.repositories.StockRepository
import com.dsvag.yandex.models.Stock
import com.dsvag.yandex.models.yandex.search.SearchRequest
import com.dsvag.yandex.models.yandex.search.SearchVariables
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val stockRepository: StockRepository,
) : ViewModel() {

    private val _stateFlow = MutableStateFlow<State>(State.Empty)
    val stateFlow: StateFlow<State> get() = _stateFlow.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.wtf("SearchViewModel", throwable)
        _stateFlow.value = when (throwable) {
            is IOException -> State.Error("Check network connection")
            is HttpException -> State.Error("Server not response. Try later")
            else -> State.Error(throwable.message.toString())
        }
    }

    fun search(query: String) {
        if (query.isEmpty()) {
            _stateFlow.value = State.Empty
        } else {
            _stateFlow.value = State.Loading
            val variables = SearchVariables(searchText = query)
            val searchRequest = SearchRequest(variables = variables)

            viewModelScope.launch(exceptionHandler) {
                val response = stockRepository.search(searchRequest)

                if (response.isEmpty()) {
                    _stateFlow.value = State.Empty
                } else {
                    _stateFlow.value = State.Success(response)
                }
            }
        }
    }

    fun changeFavoriteStatus(stock: Stock) {
        viewModelScope.launch(exceptionHandler) {
            if (stock.isFavorite) {
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