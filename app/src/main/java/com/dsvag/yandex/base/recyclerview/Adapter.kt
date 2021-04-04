package com.dsvag.yandex.base.recyclerview

import androidx.recyclerview.widget.AsyncListDiffer

class Adapter<T : ViewTyped>(holderFactory: HolderFactory) : BaseAdapter<T>(holderFactory) {

    private val stockListDiffer = AsyncListDiffer<T>(this, ViewTypedItemCallback())

    override var items: List<T>
        get() = stockListDiffer.currentList
        set(value) {
            stockListDiffer.submitList(value)
        }

    fun add(vararg newItems: T) {
        items = items.toMutableList().apply { addAll(newItems) }
    }
}