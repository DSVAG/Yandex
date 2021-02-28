package com.dsvag.yandex.ui.stockList

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dsvag.yandex.R
import com.dsvag.yandex.databinding.FragmentStockListBinding
import com.dsvag.yandex.ui.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StockListFragment : Fragment(R.layout.fragment_stock_list) {

    private val binding by viewBinding(FragmentStockListBinding::bind)

    private val stocksViewModel by viewModels<StocksViewModel>()

    private val stockAdapter by lazy { StockAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.stockList.adapter = stockAdapter

        binding.search.setOnClickListener {
            //findNavController().navigate(R.id.action_stockListFragment_to_searchFragment)
            stocksViewModel.sub()
        }


    }
}