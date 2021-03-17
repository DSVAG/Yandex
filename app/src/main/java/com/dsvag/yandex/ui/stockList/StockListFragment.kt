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

    private val stockAdapter by lazy { StockAdapter() }

    private val onTabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            when (tab.position) {
                0 -> {
                }
                1 -> {
                }
                else -> error("Unknown position")
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {}
        override fun onTabReselected(tab: TabLayout.Tab?) {}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.stockList.adapter = stockAdapter

        binding.search.setOnClickListener {
            findNavController().navigate(R.id.action_stockListFragment_to_searchFragment)
        }

        lifecycleScope.launchWhenCreated {
            stocksViewModel.stockFlow.collect { stockList ->
                stockAdapter.setData(stockList)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        stocksViewModel.subscribe()
        binding.tabLayout.addOnTabSelectedListener(onTabSelectedListener)
    }

    override fun onPause() {
        super.onPause()

        stocksViewModel.unSubscribe()
        binding.tabLayout.clearOnTabSelectedListeners()
    }
}