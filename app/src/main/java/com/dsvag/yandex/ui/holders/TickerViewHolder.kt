package com.dsvag.yandex.ui.holders

import com.dsvag.yandex.R
import com.dsvag.yandex.base.recyclerview.BaseViewHolder
import com.dsvag.yandex.base.recyclerview.ViewTyped
import com.dsvag.yandex.databinding.ItemTickerBinding

data class TickerUI(
    val ticker: String,
    override val viewType: Int = R.layout.item_ticker,
    override val uId: String = ticker
) : ViewTyped

class TickerViewHolder(
    private val itemBinding: ItemTickerBinding,
    private val onTickerClick: (String) -> Unit,
) : BaseViewHolder<TickerUI>(itemBinding.root) {

    override fun bind(item: TickerUI, oldItem: TickerUI?) {
        itemBinding.root.text = item.ticker
        itemBinding.root.setOnClickListener { onTickerClick(item.ticker) }
    }
}