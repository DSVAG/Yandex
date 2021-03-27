package com.dsvag.yandex.ui.holders

import androidx.navigation.findNavController
import com.dsvag.yandex.R
import com.dsvag.yandex.base.recyclerview.BaseViewHolder
import com.dsvag.yandex.base.recyclerview.ViewTyped
import com.dsvag.yandex.databinding.ItemNewsBinding
import com.dsvag.yandex.models.yandex.stock.response.ItemNews

data class NewsUI(
    val news: ItemNews,
    override val viewType: Int = R.layout.item_news,
    override val uId: String = news.id.toString()
) : ViewTyped

class NewsViewHolder(private val itemBinding: ItemNewsBinding) : BaseViewHolder<NewsUI>(itemBinding.root) {

    override fun bind(item: NewsUI) {
        itemBinding.root.findNavController()

        itemBinding.title.text = item.news.title
        itemBinding.preview.text = item.news.content

        // Date format   2021-02-15T09:11:00
        val date = item.news.publicationDate
            .replace('-', '.')
            .split("T")
            .asReversed()
            .joinToString(" ")

        itemBinding.dateAndSource.text = date.plus(item.news.source)
    }
}