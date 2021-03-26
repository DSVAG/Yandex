package com.dsvag.yandex.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsvag.yandex.data.repositories.StockRepository
import com.dsvag.yandex.models.Stock
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
class StocksViewModel @Inject constructor(
    private val stockRepository: StockRepository,
) : ViewModel() {

    val defaultStockFlow get() = stockRepository.defaultStockFlow

    val favoriteStockFlow get() = stockRepository.favoriteStockFlow

    private val _stateFlow = MutableStateFlow<State>(State.Default)
    val stateFlow: StateFlow<State> get() = _stateFlow.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _stateFlow.value = when (throwable) {
            is IOException -> State.Error("Check network connection")
            is HttpException -> State.Error("Server not response. Try later")
            else -> State.Error(throwable.message.toString())
        }
    }

    fun subscribe() {
        viewModelScope.launch(exceptionHandler) {
            _stateFlow.value = State.Success
            stockRepository.subscribe()
        }
    }

    fun changeFavoriteStatus(stock: Stock, isFavorite: Boolean) {
        viewModelScope.launch(exceptionHandler) {
            _stateFlow.value = State.Success
            if (isFavorite) {
                stockRepository.addToFavorite(stock)
            } else {
                stockRepository.removeFromFavorite(stock)
            }
        }
    }

    sealed class State {
        object Default : State()
        object Success : State()
        data class Error(val msg: String) : State()
    }
}