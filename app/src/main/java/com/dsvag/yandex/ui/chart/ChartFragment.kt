package com.dsvag.yandex.ui.chart

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.dsvag.yandex.R
import com.dsvag.yandex.databinding.FragmentChartBinding
import com.dsvag.yandex.ui.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChartFragment : Fragment(R.layout.fragment_chart) {

    private val binding by viewBinding(FragmentChartBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}