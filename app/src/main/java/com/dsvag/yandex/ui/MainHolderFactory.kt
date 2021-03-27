package com.dsvag.yandex.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import com.dsvag.yandex.R
import com.dsvag.yandex.base.recyclerview.BaseViewHolder
import com.dsvag.yandex.base.recyclerview.HolderFactory
import com.dsvag.yandex.databinding.*
import com.dsvag.yandex.models.Stock
import com.dsvag.yandex.ui.holders.*

class MainHolderFactory(
    private val onStockClick: (Stock) -> Unit = {},
    private val onTickerClick: (String) -> Unit = {},
) : HolderFactory() {

    override fun createViewHolder(viewGroup: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val inflater = LayoutInflater.from(viewGroup.context)

        return when (viewType) {
            R.layout.item_stock -> StockViewHolder(ItemStockBinding.inflate(inflater, viewGroup, false), onStockClick)
            R.layout.item_stock_list -> StockListViewHolder(ItemStockListBinding.inflate(inflater, viewGroup, false))
            R.layout.shimmer_stock -> ShimmerStockViewHolder(ShimmerStockBinding.inflate(inflater, viewGroup, false))
            R.layout.item_popular_stocks -> PopularStocksViewHolder(ItemPopularStocksBinding.inflate(inflater,viewGroup,false))
            R.layout.item_ticker -> TickerViewHolder(ItemTickerBinding.inflate(inflater, viewGroup, false), onTickerClick)
            R.layout.item_news -> NewsViewHolder(ItemNewsBinding.inflate(inflater, viewGroup,false))
            else -> null
        } as BaseViewHolder<*>
    }

}