package com.dsvag.yandex.ui.search

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dsvag.yandex.R
import com.dsvag.yandex.base.showToast
import com.dsvag.yandex.base.textChanges
import com.dsvag.yandex.databinding.FragmentSearchBinding
import com.dsvag.yandex.models.Stock
import com.dsvag.yandex.ui.list.StockAdapter
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

    private val stockAdapter by lazy { StockAdapter(::changeFavoriteStatus) }

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

    private fun changeFavoriteStatus(stock: Stock, isFavorite: Boolean) {
        searchViewModel.changeFavoriteStatus(stock, isFavorite)
    }

    private fun stateObserver(state: SearchViewModel.State) {
        when (state) {
            SearchViewModel.State.Empty -> {
                binding.loadingIndicator.isVisible = false
                stockAdapter.setData(emptyList())
            }
            SearchViewModel.State.Loading -> {
                binding.loadingIndicator.isVisible = true
            }
            is SearchViewModel.State.Success -> {
                binding.loadingIndicator.isVisible = false
                stockAdapter.setData(state.stocks)
            }
            is SearchViewModel.State.Error -> {
                binding.loadingIndicator.isVisible = false
                stockAdapter.setData(emptyList())
                requireContext().showToast(state.msg)
            }
        }
    }
}