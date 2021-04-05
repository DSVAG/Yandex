package com.dsvag.yandex.ui.holders

import android.graphics.Color
import coil.clear
import coil.load
import com.dsvag.yandex.R
import com.dsvag.yandex.base.Charts
import com.dsvag.yandex.base.recyclerview.BaseViewHolder
import com.dsvag.yandex.base.recyclerview.ViewTyped
import com.dsvag.yandex.databinding.ItemChartBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

data class ChartUI(
    val candles: Charts,
    override val viewType: Int = R.layout.item_chart,
    override val uId: String = candles.toString(),
) : ViewTyped

class ChartViewHolder(
    private val itemBinding: ItemChartBinding,
) : BaseViewHolder<ChartUI>(itemBinding.root) {

    private val context = itemBinding.root.context

    init {
        itemBinding.chart.apply {
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            xAxis.isEnabled = false
            legend.isEnabled = false
            description.isEnabled = false
            isDragEnabled = true

            setTouchEnabled(true)
            setScaleEnabled(true)
            setPinchZoom(true)
            setDrawMarkers(false)
            setViewPortOffsets(0F, 0F, 0F, 0F)
        }
    }

    override fun bind(item: ChartUI, oldItem: ChartUI?) {
        var data = item.candles.first.takeLast(30).map { Entry(it.first.toFloat(), it.second.toFloat()) }
        setData(data)

        itemBinding.day.setOnClickListener {
            data = item.candles.first.takeLast(30).map { Entry(it.first.toFloat(), it.second.toFloat()) }
            setData(data)
        }

        itemBinding.week.setOnClickListener {
            data = item.candles.first.map { Entry(it.first.toFloat(), it.second.toFloat()) }
            setData(data)
        }

        itemBinding.month.setOnClickListener {
            data = item.candles.second.takeLast(40).map { Entry(it.first.toFloat(), it.second.toFloat()) }
            setData(data)
        }

        itemBinding.halfYear.setOnClickListener {
            data = item.candles.second.takeLast(200).map { Entry(it.first.toFloat(), it.second.toFloat()) }
            setData(data)
        }

        itemBinding.year.setOnClickListener {
            data = item.candles.second.takeLast(400).map { Entry(it.first.toFloat(), it.second.toFloat()) }
            setData(data)
        }

        itemBinding.all.setOnClickListener {
            data = item.candles.second.map { Entry(it.first.toFloat(), it.second.toFloat()) }
            setData(data)
        }

    }

    private fun setData(data: List<Entry>) {
        val first = data.first().y
        val last = data.last().y

        val priceChange = last - first
        val priceChangePercent = (last - first) / first

        itemBinding.price.text = String.format("%.2f", last)
        itemBinding.declined.text = String.format("$%.2f (%.3f)", priceChange, priceChangePercent)

        when {
            priceChange > 0 -> {
                itemBinding.declined.setTextColor(context.getColor(R.color.green))
                itemBinding.trending.load(R.drawable.ic_trending_up)
            }
            priceChange < 0 -> {
                itemBinding.declined.setTextColor(context.getColor(R.color.red))
                itemBinding.trending.load(R.drawable.ic_trending_down)
            }
            else -> {
                itemBinding.declined.setTextColor(context.getColor(R.color.grey))
                itemBinding.trending.clear()
            }
        }

        val dataSet = LineDataSet(data, null).apply {
            color = Color.BLACK
            lineWidth = 2f
            isHighlightEnabled = true
            fillDrawable = context.getDrawable(R.drawable.bg_chart)
            mode = LineDataSet.Mode.LINEAR
            setDrawCircles(false)
            setDrawFilled(true)
            setDrawHighlightIndicators(false)
            setDrawValues(false)
        }

        itemBinding.chart.data = LineData(dataSet)
        itemBinding.chart.invalidate()
        itemBinding.chart.zoomToCenter(0.001F, 0.001F)
    }
}