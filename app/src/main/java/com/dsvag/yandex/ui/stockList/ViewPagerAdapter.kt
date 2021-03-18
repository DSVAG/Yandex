package com.dsvag.yandex.ui.stockList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dsvag.yandex.databinding.ItemListStockBinding

class ViewPagerAdapter : RecyclerView.Adapter<ViewPagerAdapter.StockListViewHolder>() {

    private val mutableAdapterList = mutableListOf<StockAdapter>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return StockListViewHolder(ItemListStockBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: StockListViewHolder, position: Int) {
        holder.bind(mutableAdapterList[position])
    }

    override fun getItemCount(): Int = 2

    fun setAdapters(vararg adapters: StockAdapter) {
        mutableAdapterList.clear()
        mutableAdapterList.addAll(adapters)

        notifyDataSetChanged()
    }

    class StockListViewHolder(
        private val itemBinding: ItemListStockBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(stockAdapter: StockAdapter) {
            itemBinding.stockList.adapter = stockAdapter
        }
    }
}