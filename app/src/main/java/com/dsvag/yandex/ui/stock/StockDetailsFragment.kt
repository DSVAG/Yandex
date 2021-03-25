package com.dsvag.yandex.ui.stock

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dsvag.yandex.R
import com.dsvag.yandex.databinding.FragmentStockDetailsBinding
import com.dsvag.yandex.ui.viewBinding

class StockDetailsFragment : Fragment(R.layout.fragment_stock_details) {


    private val binding by viewBinding(FragmentStockDetailsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.back.setOnClickListener { findNavController().popBackStack() }

    }
}