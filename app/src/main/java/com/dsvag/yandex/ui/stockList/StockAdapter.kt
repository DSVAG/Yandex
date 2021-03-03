package com.dsvag.yandex.ui.stockList

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.clear
import coil.load
import com.dsvag.yandex.R
import com.dsvag.yandex.databinding.RowStockBinding
import com.dsvag.yandex.models.Stock
import com.dsvag.yandex.ui.stockList.utils.StockDiffUtilsCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StockAdapter : RecyclerView.Adapter<StockAdapter.StockViewHolder>() {

    private val stockList: MutableList<Stock> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return StockViewHolder(RowStockBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        holder.bind(stockList[position])
    }

    override fun getItemCount(): Int = stockList.size

    suspend fun setData(newStockList: List<Stock>) = withContext(Dispatchers.Main) {
        withContext(Dispatchers.IO) {
            DiffUtil.calculateDiff(StockDiffUtilsCallback(newStockList, stockList))
        }.dispatchUpdatesTo(this@StockAdapter)

        stockList.apply {
            clear()
            addAll(newStockList)
        }
    }

    class StockViewHolder(
        private val itemBinding: RowStockBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        private val context = itemBinding.root.context

        fun bind(stock: Stock) {
            itemBinding.logo.background = context.getDrawable(R.drawable.bg_stock)
            itemBinding.logo.clipToOutline = true

            itemBinding.ticker.text = stock.ticker
            itemBinding.company.text = stock.company
            itemBinding.price.text = String.format("$%.3f", stock.lastPrice)
            itemBinding.declined.text = String.format("%.2f", stock.volume)
            itemBinding.isFavorite.isChecked = stock.isFavorite

            itemBinding.logo.load(stock.logo) {
                crossfade(true)
                error(R.drawable.ic_error)
            }

            itemBinding.root.backgroundTintList = if (adapterPosition % 2 == 0) {
                ColorStateList.valueOf(context.getColor(R.color.grey_light))
            } else {
                ColorStateList.valueOf(context.getColor(R.color.white))
            }

            when {
                stock.volume > 0 -> {
                    itemBinding.declined.setTextColor(context.getColor(R.color.green))
                    itemBinding.trending.load(R.drawable.ic_trending_up)
                }
                stock.volume < 0 -> {
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