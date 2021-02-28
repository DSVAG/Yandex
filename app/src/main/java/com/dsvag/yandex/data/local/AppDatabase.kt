package com.dsvag.yandex.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dsvag.yandex.models.StockInfo

@Database(entities = [StockInfo::class], version = 1)
abstract class AppDatabase() : RoomDatabase() {
    abstract fun stockInfoDao(): StockInfoDao
}