package com.dsvag.yandex.models

data class Stock(
    val logo: String,
    val company: String,
    val ticker: String,
    val webUrl: String,
    val lastPrice: Double,
    val symbol: String,
    val volume: Double
)