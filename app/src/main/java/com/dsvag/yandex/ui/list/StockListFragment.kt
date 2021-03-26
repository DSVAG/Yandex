package com.dsvag.yandex.ui.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dsvag.yandex.R
import com.dsvag.yandex.base.showToast
import com.dsvag.yandex.databinding.FragmentStockListBinding
import com.dsvag.yandex.models.Stock
import com.dsvag.yandex.ui.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class StockListFragment : Fragment(R.layout.fragment_stock_list) {

    private val binding by viewBinding(FragmentStockListBinding::bind)

    private val stocksViewModel by viewModels<StocksViewModel>()

    private val defaultStockAdapter by lazy(LazyThreadSafetyMode.NONE) { StockAdapter(::changeFavoriteStatus) }

    private val favoriteStockAdapter by lazy(LazyThreadSafetyMode.NONE) { StockAdapter(::changeFavoriteStatus) }

    private val viewPagerAdapter by lazy(LazyThreadSafetyMode.NONE) {
        ViewPagerAdapter().apply {
            setAdapters(defaultStockAdapter, favoriteStockAdapter)
        }
    }

    private val tabConfigurationStrategy = TabConfigurationStrategy { tab, position ->
        when (position) {
            0 -> tab.text = requireContext().getString(R.string.stocks)
            1 -> tab.text = requireContext().getString(R.string.favorite)
            else -> error("Unknown position")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager, tabConfigurationStrategy).attach()

        binding.search.setOnClickListener {
            findNavController().navigate(R.id.action_stockListFragment_to_searchFragment)
        }

        lifecycleScope.launchWhenStarted {
            stocksViewModel.defaultStockFlow.collect { stockList ->
                defaultStockAdapter.setData(stockList)
            }
        }

        lifecycleScope.launchWhenStarted {
            stocksViewModel.favoriteStockFlow.collect { stockList ->
                favoriteStockAdapter.setData(stockList)
            }
        }

        lifecycle.coroutineScope.launchWhenCreated {
            stocksViewModel.stateFlow.collect(::stateObserver)
        }
    }

    private fun changeFavoriteStatus(stock: Stock, isFavorite: Boolean) {
        stocksViewModel.changeFavoriteStatus(stock, isFavorite)
    }

    private fun stateObserver(state: StocksViewModel.State) {
        when (state) {
            StocksViewModel.State.Default -> stocksViewModel.subscribe()
            StocksViewModel.State.Success -> {
            }
            is StocksViewModel.State.Error -> requireContext().showToast(state.msg)
        }
    }

}