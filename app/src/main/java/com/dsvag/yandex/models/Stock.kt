package com.dsvag.yandex.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Stocks")
data class Stock(
    @ColumnInfo(name = "company")
    val company: String,

    @ColumnInfo(name = "logo")
    val logo: String,

    @PrimaryKey
    @ColumnInfo(name = "ticker")
    val ticker: String,

    @ColumnInfo(name = "price")
    val price: Double,

    @ColumnInfo(name = "priceChange")
    val priceChange: Double,

    @ColumnInfo(name = "priceChangePercent")
    val priceChangePercent: Double,

    @ColumnInfo(name = "isFavorite")
    val isFavorite: Boolean = false,

    @ColumnInfo(name = "isDefault")
    val isDefault: Boolean = false,
)