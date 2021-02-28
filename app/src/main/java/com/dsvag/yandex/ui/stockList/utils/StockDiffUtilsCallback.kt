package com.dsvag.yandex.ui.stockList.utils

import androidx.recyclerview.widget.DiffUtil
import com.dsvag.yandex.models.Stock

class StockDiffUtilsCallback(
    private val newList: List<Stock>,
    private val oldList: List<Stock>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].symbol == newList[newItemPosition].symbol
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}