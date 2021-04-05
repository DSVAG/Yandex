package com.dsvag.yandex.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "Stocks")
data class Stock(
    @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "company")
    val company: String,

    @ColumnInfo(name = "logo")
    val logo: String?,

    @PrimaryKey
    @ColumnInfo(name = "ticker")
    val ticker: String,

    @ColumnInfo(name = "price")
    val price: BigDecimal = BigDecimal.ZERO,

    @ColumnInfo(name = "priceChange")
    val priceChange: BigDecimal= BigDecimal.ZERO,

    @ColumnInfo(name = "priceChangePercent")
    val priceChangePercent: BigDecimal= BigDecimal.ZERO,

    @ColumnInfo(name = "isFavorite")
    val isFavorite: Boolean = false,

    @ColumnInfo(name = "isDefault")
    val isDefault: Boolean = false,
)