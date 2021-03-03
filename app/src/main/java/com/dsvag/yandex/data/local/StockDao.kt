package com.dsvag.yandex.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dsvag.yandex.models.Stock
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stock: Stock)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stocks: List<Stock>)

    @Query("SELECT * FROM Stocks WHERE isFavorite = 1")
    suspend fun getFavoriteTicker(): List<Stock>

    @Query("SELECT * FROM Stocks WHERE ticker = :ticker")
    suspend fun getStock(ticker: String): Stock?

    @Query("SELECT * FROM Stocks WHERE ticker IN (:tickers)")
    fun getStockInfoByTicker(tickers: List<String>): Flow<List<Stock>>

    @Query("SELECT * FROM Stocks")
    fun getStocks(): Flow<List<Stock>>
}