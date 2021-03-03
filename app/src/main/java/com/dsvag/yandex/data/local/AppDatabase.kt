package com.dsvag.yandex.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dsvag.yandex.models.Stock

@Database(entities = [Stock::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stockDao(): StockDao
}