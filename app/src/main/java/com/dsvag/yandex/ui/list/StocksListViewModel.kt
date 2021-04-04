package com.dsvag.yandex.ui.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsvag.yandex.base.ErrorType
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
class StocksListViewModel @Inject constructor(
    private val stockRepository: StockRepository,
) : ViewModel() {

    val defaultStockFlow get() = stockRepository.defaultStockFlow

    val favoriteStockFlow get() = stockRepository.favoriteStockFlow

    private val _stateFlow = MutableStateFlow<State>(State.Default)
    val stateFlow: StateFlow<State> get() = _stateFlow.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.wtf("StocksViewModel", throwable)
        _stateFlow.value = when (throwable) {
            is IOException -> State.Error(ErrorType.Network)
            is HttpException -> State.Error(ErrorType.Server)
            else -> State.Error(ErrorType.Other)
        }
    }

    fun subscribe() {
        viewModelScope.launch(exceptionHandler) {
            stockRepository.subscribe()
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
        object Default : State()
        data class Error(val errorType: ErrorType) : State()
    }
}