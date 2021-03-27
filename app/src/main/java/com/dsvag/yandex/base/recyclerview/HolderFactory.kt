package com.dsvag.yandex.base.recyclerview

import android.view.ViewGroup

abstract class HolderFactory : (ViewGroup, Int) -> BaseViewHolder<ViewTyped> {

    abstract fun createViewHolder(viewGroup: ViewGroup, viewType: Int): BaseViewHolder<*>

    final override fun invoke(viewGroup: ViewGroup, viewType: Int): BaseViewHolder<ViewTyped> {

        return createViewHolder(viewGroup, viewType) as BaseViewHolder<ViewTyped>
    }
}