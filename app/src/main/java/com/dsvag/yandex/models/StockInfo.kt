package com.dsvag.yandex.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "StockInfo")
data class StockInfo(
    @Json(name = "logo")
    @ColumnInfo(name = "logo")
    val logo: String,

    @Json(name = "name")
    @ColumnInfo(name = "company")
    val company: String,

    @Json(name = "ticker")
    @ColumnInfo(name = "ticker")
    @PrimaryKey
    val ticker: String,

    @Json(name = "weburl")
    @ColumnInfo(name = "weburl")
    val webUrl: String
)