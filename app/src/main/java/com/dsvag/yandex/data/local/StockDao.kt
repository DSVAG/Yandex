package com.dsvag.yandex.data.local

import androidx.room.*
import com.dsvag.yandex.models.Stock
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stock: Stock)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stocks: List<Stock>)

    @Update
    suspend fun update(stock: Stock)

    @Delete
    suspend fun delete(stock: Stock)

    @Query("SELECT * FROM Stocks WHERE ticker = :ticker")
    suspend fun getStock(ticker: String): Stock?

    @Query("SELECT ticker FROM stocks WHERE isFavorite = 1")
    suspend fun getFavoriteTickers(): List<String>

    @Query("SELECT ticker FROM stocks")
    suspend fun getTickers(): List<String>

    @Query("SELECT * FROM Stocks WHERE isFavorite = 1")
    fun getFavoriteStock(): Flow<List<Stock>>

    @Query("SELECT * FROM Stocks WHERE isDefault = 1")
    fun getDefaultStocks(): Flow<List<Stock>>

    @Query("SELECT * FROM Stocks")
    fun getStock(): Flow<List<Stock>>
}