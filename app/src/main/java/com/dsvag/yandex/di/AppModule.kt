package com.dsvag.yandex.di

import com.dsvag.yandex.data.remote.StockWebSocketListener
import com.dsvag.yandex.data.repositoyes.StockRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideRequest() = Request
        .Builder()
        .url("wss://ws.finnhub.io?token=c0ru8bf48v6r6pnh9v00")
        .build()

    @Provides
    fun provideOkHttpClient() = OkHttpClient
        .Builder()
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    @Provides
    fun provideStockWebSocketListener() = StockWebSocketListener()

    @Provides
    fun provideWebSocket(
        okHttpClient: OkHttpClient,
        request: Request,
        stockWebSocketListener: StockWebSocketListener
    ): WebSocket {
        return okHttpClient.newWebSocket(request, stockWebSocketListener)
    }

    @Provides
    fun provideStockRepository(stockWebSocketListener: StockWebSocketListener, webSocket: WebSocket): StockRepository {
        return StockRepository(stockWebSocketListener, webSocket)
    }
}