package com.dsvag.yandex.ui.stockList

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dsvag.yandex.R
import com.dsvag.yandex.databinding.RowStockBinding
import com.dsvag.yandex.models.Stock
import com.dsvag.yandex.ui.stockList.utils.StockDiffUtilsCallback

class StockAdapter : RecyclerView.Adapter<StockAdapter.StockViewHolder>() {

    private val stockList: MutableList<Stock> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return StockViewHolder(RowStockBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        holder.bind(stockList[position], position % 2 == 0)
    }

    override fun getItemCount(): Int = stockList.size

    fun setData(newStockList: List<Stock>) {
        DiffUtil.calculateDiff(StockDiffUtilsCallback(newStockList, stockList)).dispatchUpdatesTo(this)

        stockList.apply {
            clear()
            addAll(newStockList)
        }
    }

    class StockViewHolder(
        private val itemBinding: RowStockBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        private val context = itemBinding.root.context

        fun bind(stock: Stock, isEven: Boolean) {
            itemBinding.logo.background = context.getDrawable(R.drawable.bg_stock)
            itemBinding.logo.clipToOutline = true

            itemBinding.ticker.text = stock.symbol
            itemBinding.company.text = stock.company
            itemBinding.price.text = String.format("$%.3f", stock.lastPrice)
            itemBinding.declined.text = String.format("%.2f", stock.volume)

            itemBinding.logo.load(stock.logo) {
                crossfade(true)
                error(R.drawable.ic_error)
            }

            if (isEven) {
                itemBinding.root.backgroundTintList =
                    ColorStateList.valueOf(context.getColor(R.color.grey_light))
            } else {
                itemBinding.root.backgroundTintList =
                    ColorStateList.valueOf(context.getColor(R.color.white))
            }

            if (stock.volume > 0) {
                itemBinding.declined.setTextColor(context.getColor(R.color.green))
                itemBinding.trending.load(R.drawable.ic_trending_up)
            } else {
                itemBinding.declined.setTextColor(context.getColor(R.color.red))
                itemBinding.trending.load(R.drawable.ic_trending_down)
            }
        }
    }
}