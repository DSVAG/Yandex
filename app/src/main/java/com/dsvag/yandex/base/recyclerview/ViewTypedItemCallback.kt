package com.dsvag.yandex.base.recyclerview

import androidx.recyclerview.widget.DiffUtil

class ViewTypedItemCallback : DiffUtil.ItemCallback<ViewTyped>() {
    override fun areItemsTheSame(oldItem: ViewTyped, newItem: ViewTyped): Boolean = oldItem.uId == newItem.uId

    override fun areContentsTheSame(oldItem: ViewTyped, newItem: ViewTyped): Boolean = oldItem == newItem
}