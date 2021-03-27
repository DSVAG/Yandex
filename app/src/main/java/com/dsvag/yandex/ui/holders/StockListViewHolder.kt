package com.dsvag.yandex.ui.holders

import com.dsvag.yandex.R
import com.dsvag.yandex.base.recyclerview.Adapter
import com.dsvag.yandex.base.recyclerview.BaseViewHolder
import com.dsvag.yandex.base.recyclerview.ViewTyped
import com.dsvag.yandex.databinding.ItemStockListBinding

data class StockListUI(
    val adapter: Adapter<ViewTyped>,
    override val viewType: Int = R.layout.item_stock_list,
    override val uId: String,
) : ViewTyped

class StockListViewHolder(
    private val itemBinding: ItemStockListBinding,
) : BaseViewHolder<StockListUI>(itemBinding.root) {

    override fun bind(item: StockListUI) {
        itemBinding.stockList.adapter = item.adapter
    }
}