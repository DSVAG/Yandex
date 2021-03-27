package com.dsvag.yandex.ui.holders

import android.content.res.ColorStateList
import com.dsvag.yandex.R
import com.dsvag.yandex.base.recyclerview.BaseViewHolder
import com.dsvag.yandex.base.recyclerview.ViewTyped
import com.dsvag.yandex.databinding.ShimmerStockBinding

data class ShimmerStockUI(
    override val viewType: Int = R.layout.shimmer_stock,
    override val uId: String
) : ViewTyped

class ShimmerStockViewHolder(
    private val itemBinding: ShimmerStockBinding,
) : BaseViewHolder<ShimmerStockUI>(itemBinding.root) {

    private val context = itemBinding.root.context

    override fun bind(item: ShimmerStockUI) {
        itemBinding.root.startShimmer()

        itemBinding.root.backgroundTintList = if (adapterPosition % 2 == 0) {
            ColorStateList.valueOf(context.getColor(R.color.grey_light))
        } else {
            ColorStateList.valueOf(context.getColor(R.color.white))
        }
    }
}