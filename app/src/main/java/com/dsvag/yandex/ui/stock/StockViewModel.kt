package com.dsvag.yandex.ui.stock

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsvag.yandex.data.repositories.StockRepository
import com.dsvag.yandex.models.yandex.chart.CandleSize
import com.dsvag.yandex.models.yandex.chart.ChartRequest
import com.dsvag.yandex.models.yandex.chart.ChartVariables
import com.dsvag.yandex.models.yandex.chart.response.Candles
import com.dsvag.yandex.models.yandex.stock.StockRequest
import com.dsvag.yandex.models.yandex.stock.StockVariables
import com.dsvag.yandex.models.yandex.stock.response.StockResponse
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
class StockViewModel @Inject constructor(
    private val stockRepository: StockRepository,
) : ViewModel() {

    private val _stateFlow = MutableStateFlow<State>(State.Loading)
    val stateFlow: StateFlow<State> get() = _stateFlow.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.wtf("StockViewModel", throwable)
        _stateFlow.value = when (throwable) {
            is IOException -> State.Error("Check network connection")
            is HttpException -> State.Error("Server not response. Try later")
            else -> State.Error(throwable.message.toString())
        }
    }

    fun fetchStock(ticker: String) {
        _stateFlow.value = State.Loading

        val variables = StockVariables(slug = ticker)
        val apiRequest = StockRequest(variables = variables)

        viewModelScope.launch(exceptionHandler) {
            val response = stockRepository.fetchStock(apiRequest)
            val charts = fetchCharts(response.data.instruments.metaData.id, response.data.instruments.metaData.exchange)

            _stateFlow.value = State.Success(response, charts)
        }
    }

    private suspend fun fetchCharts(id: Long, exchange: String): List<Candles> {
        val charts = listOf(
            ChartVariables(CandleSize.FIVE_MINUTE, instrumentId = id, count = 290, exchange = exchange),
            ChartVariables(CandleSize.ONE_HOUR, instrumentId = id, count = 40, exchange = exchange),
            ChartVariables(CandleSize.ONE_DAY, instrumentId = id, count = 10000, exchange = exchange),
        )

        return charts.map { stockRepository.fetchStockChart(ChartRequest(variables = it)).data.candles }
    }

    sealed class State {
        object Loading : State()

        data class Success(val stockResponse: StockResponse, val charts: List<Candles>) : State()
        data class Error(val msg: String) : State()
    }
}