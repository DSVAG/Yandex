package com.dsvag.yandex.ui.stockList

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dsvag.yandex.R
import com.dsvag.yandex.databinding.FragmentStockListBinding
import com.dsvag.yandex.ui.viewBinding
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class StockListFragment : Fragment(R.layout.fragment_stock_list) {

    private val binding by viewBinding(FragmentStockListBinding::bind)

    private val stocksViewModel by viewModels<StocksViewModel>()

    private val viewPagerAdapter by lazy(LazyThreadSafetyMode.NONE) { ViewPagerAdapter() }

    private val defaultStockAdapter by lazy(LazyThreadSafetyMode.NONE) { StockAdapter({}, ::changeFavoriteStatus) }

    private val favoriteStockAdapter by lazy(LazyThreadSafetyMode.NONE) { StockAdapter({}, ::changeFavoriteStatus) }

    private val onTabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            binding.viewPager.setCurrentItem(tab.position, true)
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {}
        override fun onTabReselected(tab: TabLayout.Tab?) {}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewPagerAdapter.setAdapters(defaultStockAdapter, favoriteStockAdapter)
        binding.viewPager.adapter = viewPagerAdapter

        binding.tabLayout.addOnTabSelectedListener(onTabSelectedListener)

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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        stocksViewModel.subscribe()
    }

    private fun changeFavoriteStatus(ticker: String, isFavorite: Boolean) {
        stocksViewModel.changeFavoriteStatus(ticker, isFavorite)
    }

}