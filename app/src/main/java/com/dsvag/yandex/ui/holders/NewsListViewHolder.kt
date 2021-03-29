package com.dsvag.yandex.ui.holders

import com.dsvag.yandex.R
import com.dsvag.yandex.base.recyclerview.Adapter
import com.dsvag.yandex.base.recyclerview.BaseViewHolder
import com.dsvag.yandex.base.recyclerview.ViewTyped
import com.dsvag.yandex.databinding.ItemNewsListBinding

data class NewsListUI(
    val adapter: Adapter<ViewTyped>,
    override val viewType: Int = R.layout.item_news_list,
    override val uId: String = "newList"
) : ViewTyped

class NewsListViewHolder(
    private val itemBinding: ItemNewsListBinding
) : BaseViewHolder<NewsListUI>(itemBinding.root) {

    override fun bind(item: NewsListUI) {
        itemBinding.newsList.adapter = item.adapter
    }
}