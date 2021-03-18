package com.dsvag.yandex.ui.stockList

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import coil.clear
import coil.decode.SvgDecoder
import coil.load
import coil.transform.RoundedCornersTransformation
import com.dsvag.yandex.R
import com.dsvag.yandex.databinding.ItemStockBinding
import com.dsvag.yandex.models.Stock
import com.dsvag.yandex.ui.stockList.utils.StockDiffCallback

class StockAdapter(
    private val favoriteClick: (String, Boolean) -> Unit,
) : RecyclerView.Adapter<StockAdapter.StockViewHolder>() {

    private val stockListDiffer = AsyncListDiffer(this, StockDiffCallback())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return StockViewHolder(
            ItemStockBinding.inflate(inflater, parent, false),
            favoriteClick,
        )
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        holder.bind(stockListDiffer.currentList[position])
    }

    override fun getItemCount(): Int = stockListDiffer.currentList.size

    fun setData(newStockList: List<Stock>) {
        stockListDiffer.submitList(newStockList)
    }

    class StockViewHolder(
        private val itemBinding: ItemStockBinding,
        private val favoriteClick: (String, Boolean) -> Unit,
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        private val context = itemBinding.root.context

        fun bind(stock: Stock) {
            itemBinding.ticker.text = stock.ticker
            itemBinding.company.text = stock.company
            itemBinding.price.text = String.format("$%.3f", stock.price)
            itemBinding.declined.text = String.format("$%.2f (%.3f)", stock.priceChange, stock.priceChangePercent)

            itemBinding.isFavorite.setOnCheckedChangeListener { _, isChecked ->
                favoriteClick(stock.ticker, isChecked)
            }

            itemBinding.root.setOnClickListener { view ->
                val bundle = Bundle().apply { putString("ticker", stock.ticker) }
                view.findNavController().navigate(R.id.stockDetailsFragment, bundle)
            }

            itemBinding.isFavorite.isChecked = stock.isFavorite

            itemBinding.logo.load(stock.logo) {
                crossfade(true)
                decoder(SvgDecoder(context))
                error(R.drawable.ic_error)
                transformations(RoundedCornersTransformation(48F))
            }

            itemBinding.root.backgroundTintList = if (adapterPosition % 2 == 0) {
                ColorStateList.valueOf(context.getColor(R.color.grey_light))
            } else {
                ColorStateList.valueOf(context.getColor(R.color.white))
            }

            when {
                stock.priceChange > 0 -> {
                    itemBinding.declined.setTextColor(context.getColor(R.color.green))
                    itemBinding.trending.load(R.drawable.ic_trending_up)
                }
                stock.priceChange < 0 -> {
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
}