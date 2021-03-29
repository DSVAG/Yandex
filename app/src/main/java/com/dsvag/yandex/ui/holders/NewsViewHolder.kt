package com.dsvag.yandex.ui.holders

import android.content.res.ColorStateList
import android.text.Html
import com.dsvag.yandex.R
import com.dsvag.yandex.base.recyclerview.BaseViewHolder
import com.dsvag.yandex.base.recyclerview.ViewTyped
import com.dsvag.yandex.databinding.ItemNewsBinding
import com.dsvag.yandex.models.yandex.stock.response.Item
import java.text.SimpleDateFormat

data class NewsUI(
    val news: Item,
    override val viewType: Int = R.layout.item_news,
    override val uId: String = news.id.toString()
) : ViewTyped

class NewsViewHolder(private val itemBinding: ItemNewsBinding) : BaseViewHolder<NewsUI>(itemBinding.root) {

    private val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    private val outputFormat = SimpleDateFormat.getDateTimeInstance()

    private val context = itemBinding.root.context

    override fun bind(item: NewsUI) {
        itemBinding.root.backgroundTintList = if (adapterPosition % 2 == 0) {
            ColorStateList.valueOf(context.getColor(R.color.grey_light))
        } else {
            ColorStateList.valueOf(context.getColor(R.color.white))
        }

        itemBinding.title.text = item.news.title
        itemBinding.preview.text = Html.fromHtml(item.news.content, Html.FROM_HTML_MODE_COMPACT)

        itemBinding.source.text = item.news.source

        inputFormat.parse(item.news.publicationDate)?.let {
            itemBinding.date.text = outputFormat.format(it)
        }
    }
}