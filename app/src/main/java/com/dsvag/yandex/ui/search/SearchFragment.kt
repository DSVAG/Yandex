package com.dsvag.yandex.ui.search

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dsvag.yandex.R
import com.dsvag.yandex.databinding.FragmentSearchBinding
import com.dsvag.yandex.ui.stockList.StockAdapter
import com.dsvag.yandex.ui.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    private val binding by viewBinding(FragmentSearchBinding::bind)

    private val searchViewModel by viewModels<StockSearchViewModel>()

    private val stockAdapter by lazy { StockAdapter({ ticker, isFavorite -> }) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.searchStockList.adapter = stockAdapter
        binding.back.setOnClickListener { findNavController().popBackStack() }
        binding.clear.setOnClickListener { binding.search.text?.clear() }

        binding.search.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                searchViewModel.search(text.trim().toString())
            }
        }

    }
}