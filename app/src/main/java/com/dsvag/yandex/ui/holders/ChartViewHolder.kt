package com.dsvag.yandex.ui.holders

import android.graphics.Color
import com.dsvag.yandex.R
import com.dsvag.yandex.base.recyclerview.BaseViewHolder
import com.dsvag.yandex.base.recyclerview.ViewTyped
import com.dsvag.yandex.databinding.ItemChartBinding
import com.dsvag.yandex.models.yandex.chart.response.Candles
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

data class ChartsUI(
    val candles: List<Candles>,
    override val viewType: Int = R.layout.item_chart,
    override val uId: String = candles.toString()
) : ViewTyped

class ChartViewHolder(
    private val itemBinding: ItemChartBinding,
) : BaseViewHolder<ChartsUI>(itemBinding.root) {

    private val context = itemBinding.root.context

    init {
        itemBinding.chart.apply {
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            xAxis.isEnabled = false
            legend.isEnabled = false
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = false
            setScaleEnabled(false)
            setPinchZoom(false)
            setDrawMarkers(true)
            setViewPortOffsets(0F, 0F, 0F, 0F)
        }
    }

    override fun bind(item: ChartsUI) {
        val data = item.candles.first().seriesBefore.map { Entry(it[0].toFloat(), it[1].toFloat()) }

        val dataSet = LineDataSet(data, "Unused label").apply {
            color = Color.BLACK
            setDrawValues(true)
            lineWidth = 2f
            isHighlightEnabled = true
            setDrawCircles(false)
            setDrawFilled(true)
            fillDrawable = context.getDrawable(R.drawable.bg_chart)
            setDrawHighlightIndicators(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            cubicIntensity = 0.1f
        }

        itemBinding.chart.data = LineData(dataSet)
        itemBinding.chart.invalidate()
    }

}