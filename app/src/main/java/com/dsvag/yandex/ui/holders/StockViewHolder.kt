package com.dsvag.yandex.ui.holders

import android.content.res.ColorStateList
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import coil.clear
import coil.decode.SvgDecoder
import coil.load
import coil.transform.RoundedCornersTransformation
import com.dsvag.yandex.R
import com.dsvag.yandex.base.recyclerview.BaseViewHolder
import com.dsvag.yandex.base.recyclerview.ViewTyped
import com.dsvag.yandex.databinding.ItemStockBinding
import com.dsvag.yandex.models.Stock
import java.math.BigDecimal

data class StockUI(
    val stock: Stock,
    override val viewType: Int = R.layout.item_stock,
    override val uId: String = stock.ticker
) : ViewTyped

class StockViewHolder(
    private val itemBinding: ItemStockBinding,
    private val favoriteClick: (Stock) -> Unit
) : BaseViewHolder<StockUI>(itemBinding.root) {

    private val context = itemBinding.root.context

    override fun bind(item: StockUI, oldItem: StockUI?) {
        val stock = item.stock
        val oldStock = oldItem?.stock

        itemBinding.ticker.text = stock.ticker
        itemBinding.company.text = stock.company
        itemBinding.price.text = String.format("$%.2f", stock.price)
        itemBinding.declined.text = String.format("$%.2f (%.3f)", stock.priceChange, stock.priceChangePercent)

        itemBinding.isFavorite.setOnCheckedChangeListener { _, isChecked ->
            favoriteClick(stock.copy(isFavorite = isChecked))
        }

        itemBinding.root.setOnClickListener { view ->
            view.findNavController().navigate(R.id.stockDetailsFragment, bundleOf("ticker" to stock.ticker))
        }

        itemBinding.isFavorite.isChecked = stock.isFavorite

        if (stock.logo != oldStock?.logo) {
            itemBinding.logo.load("https://yastatic.net/s3/fintech-icons/1/i/${stock.logo}.svg") {
                crossfade(true)
                decoder(SvgDecoder(context))
                error(R.drawable.ic_error)
                transformations(RoundedCornersTransformation(48F))
            }
        }

        itemBinding.root.backgroundTintList = if (adapterPosition % 2 == 0) {
            ColorStateList.valueOf(context.getColor(R.color.grey_light))
        } else {
            ColorStateList.valueOf(context.getColor(R.color.white))
        }

        when {
            stock.priceChange > BigDecimal.ZERO -> {
                itemBinding.declined.setTextColor(context.getColor(R.color.green))
                itemBinding.trending.load(R.drawable.ic_trending_up)
            }
            stock.priceChange < BigDecimal.ZERO -> {
                itemBinding.declined.setTextColor(context.getColor(R.color.red))
                itemBinding.trending.load(R.drawable.ic_trending_down)
            }
            else -> {
                itemBinding.declined.setTextColor(context.getColor(R.color.grey))
                itemBinding.trending.clear()
            }
        }
    }
}