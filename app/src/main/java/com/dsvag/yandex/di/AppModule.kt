package com.dsvag.yandex.di

import android.content.Context
import androidx.room.Room
import com.dsvag.yandex.data.local.AppDatabase
import com.dsvag.yandex.data.remote.YandexApi
import com.dsvag.yandex.data.repositories.StockChangesObserver
import com.dsvag.yandex.data.repositories.StockRepository
import com.squareup.moshi.*
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
        return Request.Builder()
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

    @OptIn(ExperimentalStdlibApi::class)
    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .addAdapter(object : JsonAdapter<Pair<Long, Double>>() {
                override fun fromJson(reader: JsonReader): Pair<Long, Double> {
                    reader.beginArray()
                    val long = reader.nextLong()
                    val double = reader.nextDouble()
                    reader.endArray()
                    return Pair(long, double)
                }

                override fun toJson(writer: JsonWriter, value: Pair<Long, Double>?) {
                    if (value == null) {
                        writer.nullValue()
                    } else {
                        writer.beginArray()
                        writer.value(value.first)
                        writer.value(value.second)
                        writer.endArray()
                    }
                }
            })
            .build()
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
        return Room.databaseBuilder(context, AppDatabase::class.java, "Stock.db").build()
    }

    @Provides
    fun provideStockRepository(
        yandexApi: YandexApi,
        appDatabase: AppDatabase,
        stockChangesObserver: StockChangesObserver,
    ): StockRepository {
        return StockRepository(yandexApi, appDatabase.stockDao(), stockChangesObserver)
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Provides
    fun provideStockChangesObserver(
        okHttpClient: OkHttpClient,
        request: Request,
        moshi: Moshi
    ): StockChangesObserver {
        return StockChangesObserver(
            okHttpClient,
            request,
            stockDataResponseJsonAdapter = moshi.adapter(),
            socketMsgJsonAdapter = moshi.adapter(),
        )
    }
}