package com.dsvag.yandex.base.recyclerview

interface ViewTyped {
    val viewType: Int
        get() = error("provide viewType $this")

    val uId: String
        get() = error("provide uId for viewType $this")
}