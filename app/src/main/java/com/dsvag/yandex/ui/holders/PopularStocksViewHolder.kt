package com.dsvag.yandex.ui.holders

import com.dsvag.yandex.R
import com.dsvag.yandex.base.recyclerview.Adapter
import com.dsvag.yandex.base.recyclerview.BaseViewHolder
import com.dsvag.yandex.base.recyclerview.ViewTyped
import com.dsvag.yandex.databinding.ItemPopularStocksBinding

data class PopularStocksUI(
    val adapter: Adapter<ViewTyped>,
    override val viewType: Int = R.layout.item_popular_stocks,
    override val uId: String = "popular"
) : ViewTyped

class PopularStocksViewHolder(
    private val itemBiding: ItemPopularStocksBinding
) : BaseViewHolder<PopularStocksUI>(itemBiding.root) {

    override fun bind(item: PopularStocksUI, oldItem: PopularStocksUI?) {
        itemBiding.tickerList.adapter = item.adapter
    }
}