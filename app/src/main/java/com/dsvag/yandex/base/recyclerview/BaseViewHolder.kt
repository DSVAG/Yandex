package com.dsvag.yandex.base.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<T : ViewTyped>(view: View) : RecyclerView.ViewHolder(view) {

    abstract fun bind(item: T, oldItem: T? = null)

}