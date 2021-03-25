package com.dsvag.yandex.ui.list.utils

import androidx.recyclerview.widget.DiffUtil
import com.dsvag.yandex.models.Stock

class StockDiffCallback : DiffUtil.ItemCallback<Stock>() {

    override fun areItemsTheSame(oldItem: Stock, newItem: Stock): Boolean = oldItem.ticker == newItem.ticker

    override fun areContentsTheSame(oldItem: Stock, newItem: Stock): Boolean = oldItem.price == newItem.price

}