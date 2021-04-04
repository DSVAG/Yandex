package com.dsvag.yandex.ui.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dsvag.yandex.R
import com.dsvag.yandex.base.ErrorType
import com.dsvag.yandex.base.recyclerview.Adapter
import com.dsvag.yandex.base.recyclerview.ViewTyped
import com.dsvag.yandex.base.showToast
import com.dsvag.yandex.base.textChanges
import com.dsvag.yandex.databinding.FragmentSearchBinding
import com.dsvag.yandex.models.Stock
import com.dsvag.yandex.ui.MainHolderFactory
import com.dsvag.yandex.ui.holders.PopularStocksUI
import com.dsvag.yandex.ui.holders.ShimmerStockUI
import com.dsvag.yandex.ui.holders.StockUI
import com.dsvag.yandex.ui.holders.TickerUI
import com.dsvag.yandex.ui.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    private val binding by viewBinding(FragmentSearchBinding::bind)

    private val searchViewModel by viewModels<SearchViewModel>()

    private val stockAdapter by lazy(LazyThreadSafetyMode.NONE) {
        Adapter<ViewTyped>(MainHolderFactory(onStockClick = ::changeFavoriteStatus))
    }

    private val popularStockAdapter by lazy(LazyThreadSafetyMode.NONE) {
        Adapter<ViewTyped>(MainHolderFactory(onTickerClick = ::onTickerClick)).apply { items = popularTicker }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.searchStockList.adapter = stockAdapter
        binding.back.setOnClickListener { findNavController().popBackStack() }
        binding.clear.setOnClickListener { binding.search.text?.clear() }

        binding.search.textChanges()
            .debounce(300)
            .onEach { searchViewModel.search(it.toString()) }
            .launchIn(lifecycleScope)

        lifecycleScope.launchWhenCreated {
            searchViewModel.stateFlow.collect(::stateObserver)
        }
    }

    private fun changeFavoriteStatus(stock: Stock) {
        searchViewModel.changeFavoriteStatus(stock)
    }

    private fun onTickerClick(ticker: String) {
        binding.search.setText(ticker)
    }

    private fun stateObserver(state: SearchViewModel.State) {
        when (state) {
            SearchViewModel.State.Empty -> {
                stockAdapter.items = listOf(PopularStocksUI(popularStockAdapter))
            }
            SearchViewModel.State.Loading -> {
                stockAdapter.items = shimmersList
            }
            is SearchViewModel.State.Success -> {
                stockAdapter.items = state.stocks.map { StockUI(it) }
            }
            is SearchViewModel.State.Error -> throwError(state.errorType)

        }
    }

    private fun throwError(errorType: ErrorType) {
        val msg = when (errorType) {
            ErrorType.Network -> requireContext().getString(R.string.error_network)
            ErrorType.Server -> requireContext().getString(R.string.error_server)
            else -> requireContext().getString(R.string.error_unknown)
        }
        stockAdapter.items = emptyList()

        requireContext().showToast(msg)
        findNavController().popBackStack()
    }

    private companion object {
        val shimmersList = listOf(
            ShimmerStockUI(uId = "0"), ShimmerStockUI(uId = "1"), ShimmerStockUI(uId = "2"),
            ShimmerStockUI(uId = "3"), ShimmerStockUI(uId = "4")
        )

        val popularTicker = listOf(
            TickerUI("Apple"), TickerUI("Amazon"), TickerUI("Google"), TickerUI("Tesla"),
            TickerUI("Facebook"), TickerUI("Mastercard"), TickerUI("Alibaba"), TickerUI("GM"),
            TickerUI("Microsoft"), TickerUI("Visa"), TickerUI("Intel"),
        )
    }
}