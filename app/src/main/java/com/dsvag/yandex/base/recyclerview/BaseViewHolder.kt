package com.dsvag.yandex.base.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView

open class BaseViewHolder<T : ViewTyped>(view: View) : RecyclerView.ViewHolder(view) {

    open fun bind(item: T) {}

    open fun bind(item: T, payloads: List<Any>) {}

}