package com.dsvag.yandex.ui.stock

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dsvag.yandex.R
import com.dsvag.yandex.base.launchWhenCreated
import com.dsvag.yandex.base.showToast
import com.dsvag.yandex.databinding.FragmentStockDetailsBinding
import com.dsvag.yandex.models.yandex.stock.response.StockResponse
import com.dsvag.yandex.ui.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class StockDetailsFragment : Fragment(R.layout.fragment_stock_details) {

    private val binding by viewBinding(FragmentStockDetailsBinding::bind)

    private val stockViewModel by viewModels<StockViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.back.setOnClickListener { findNavController().popBackStack() }

        stockViewModel.stateFlow.onEach(::stateObserver).launchWhenCreated(lifecycleScope)

    }

    override fun onStart() {
        super.onStart()

        arguments?.getString("ticker")?.let { stockViewModel.fetchStock(it) }
            ?: run { findNavController().popBackStack() }
    }

    private fun stateObserver(state: StockViewModel.State) {
        when (state) {
            StockViewModel.State.Loading -> {
            }
            is StockViewModel.State.Success -> setData(state.stockResponse)
            is StockViewModel.State.Error -> {
                requireContext().showToast(state.msg)
                findNavController().popBackStack()
            }
        }
    }

    private fun setData(stockResponse: StockResponse) {
        binding.ticker.text = stockResponse.data.instruments.metaData.ticker
        binding.company.text = stockResponse.data.instruments.metaData.displayName
    }
}