package com.dsvag.yandex.ui.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsvag.yandex.data.repositories.StockRepository
import com.dsvag.yandex.models.Stock
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _stateFlow = MutableStateFlow<StocksListState>(StocksListState.Default)
    val stateFlow: StateFlow<StocksListState> get() = _stateFlow

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        when (throwable) {
            is IOException -> {
                Log.e(TAG, "IOException", throwable)
                _stateFlow.value = StocksListState.Error("Check network connection")
            }
            is HttpException -> {
                Log.e(TAG, "HttpException", throwable)
                _stateFlow.value = StocksListState.Error("Server not response. Try later")
            }
            else -> {
                Log.e(TAG, throwable.message, throwable)
                _stateFlow.value = StocksListState.Error(throwable.message.toString())
            }
        }
    }

    fun subscribe() {
        viewModelScope.launch(exceptionHandler) {
            _stateFlow.value = StocksListState.Success
            stockRepository.subscribe()
        }
    }

    fun changeFavoriteStatus(stock: Stock, isFavorite: Boolean) {
        viewModelScope.launch(exceptionHandler) {
            _stateFlow.value = StocksListState.Success
            if (isFavorite) {
                stockRepository.addToFavorite(stock)
            } else {
                stockRepository.removeFromFavorite(stock)
            }
        }
    }

    sealed class StocksListState {
        object Default : StocksListState()
        object Success : StocksListState()
        data class Error(val msg: String) : StocksListState()
    }

    private companion object {
        const val TAG = "StocksViewModel"
    }
}