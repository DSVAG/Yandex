package com.dsvag.yandex.base.recyclerview

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

class ViewTypedItemCallback<T : ViewTyped> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem.uId == newItem.uId

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem

    override fun getChangePayload(oldItem: T, newItem: T) = oldItem
}