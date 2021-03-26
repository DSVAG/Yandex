package com.dsvag.yandex.di

import android.content.Context
import androidx.room.Room
import com.dsvag.yandex.data.local.AppDatabase
import com.dsvag.yandex.data.remote.FinnhubApi
import com.dsvag.yandex.data.remote.YandexApi
import com.dsvag.yandex.data.repositories.StockRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideRequest(): Request {
        return Request
            .Builder()
            .url("wss://ws.finnhub.io")
            .addHeader("X-Finnhub-Token", "c0ru8bf48v6r6pnh9v00")
            .build()
    }

    @Provides
    fun provideInterceptor(): Interceptor {
        return Interceptor { chain ->

            val url = chain
                .request()
                .url
                .newBuilder()
                .addQueryParameter("token", "c0ru8bf48v6r6pnh9v00")
                .build()

            val request = chain
                .request()
                .newBuilder()
                .url(url)
                .addHeader("X-Finnhub-Token", "c0ru8bf48v6r6pnh9v00")
                .build()

            chain.proceed(request)
        }
    }

    @Provides
    fun provideOkHttpClient(requestInterceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(requestInterceptor)
            .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    }

    @Provides
    fun provideFinnhubApi(moshi: Moshi, okHttpClient: OkHttpClient): FinnhubApi {
        return Retrofit.Builder()
            .baseUrl("https://finnhub.io/api/v1/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
            .create(FinnhubApi::class.java)
    }

    @Provides
    fun provideYandexApi(moshi: Moshi): YandexApi {
        val interceptor = Interceptor { chain ->
            val url = chain
                .request()
                .url
                .newBuilder()
                .build()

            val request = chain
                .request()
                .newBuilder()
                .url(url)
                .addHeader("Cookie", "yandexuid=2961259081616698918")
                .addHeader("x-csrf-token", "1e0a055e1af4e5c061195f9443131bf7b7a25a94:1616771663")
                .build()

            chain.proceed(request)
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

        return Retrofit.Builder()
            .baseUrl("https://plus.yandex.ru/invest/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
            .create(YandexApi::class.java)
    }

    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room
            .databaseBuilder(context, AppDatabase::class.java, "Stock.db")
            .build()
    }

    @Provides
    fun provideStockRepository(
        finnhubApi: FinnhubApi,
        yandexApi: YandexApi,
        appDatabase: AppDatabase,
        okHttpClient: OkHttpClient,
        request: Request,
    ): StockRepository {
        return StockRepository(finnhubApi, yandexApi, appDatabase.stockDao(), okHttpClient, request)
    }
}