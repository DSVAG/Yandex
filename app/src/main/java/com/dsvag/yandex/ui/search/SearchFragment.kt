package com.dsvag.yandex.ui.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dsvag.yandex.R
import com.dsvag.yandex.databinding.FragmentSearchBinding
import com.dsvag.yandex.ui.viewBinding

//Maybe bottomSheet?
class SearchFragment : Fragment(R.layout.fragment_search) {

    private val binding by viewBinding(FragmentSearchBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.back.setOnClickListener { findNavController().popBackStack() }
        binding.clear.setOnClickListener { binding.search.text?.clear() }

    }
}