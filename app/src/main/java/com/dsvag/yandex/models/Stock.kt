package com.dsvag.yandex.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Stocks")
data class Stock(
    val logo: String,

    @ColumnInfo(name = "company")
    val company: String,

    @PrimaryKey
    @ColumnInfo(name = "ticker")
    val ticker: String,

    @ColumnInfo(name = "webUrl")
    val webUrl: String,

    @ColumnInfo(name = "lastPrice")
    val lastPrice: Double,

    @ColumnInfo(name = "volume")
    val volume: Double,

    @ColumnInfo(name = "isFavorite")
    val isFavorite: Boolean
)