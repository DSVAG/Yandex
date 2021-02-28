package com.dsvag.yandex.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dsvag.yandex.models.StockInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface StockInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stockInfo: StockInfo)

    @Query("SELECT * FROM StockInfo WHERE ticker (:tickers)")
    fun getStockInfoByTicker(tickers: List<String>): Flow<List<StockInfo>>
}