package com.example.hotelapp.di

import com.example.hotelapp.BuildConfig
import com.example.hotelapp.data.remote.HotelsApiService
import com.example.hotelapp.data.remote.dto.HotelsSearchResponse
import com.example.hotelapp.data.remote.dto.HotelsSearchResponseDeserializer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import dagger.Module
import java.lang.reflect.Type
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private const val BASE_URL = "https://api.hotels-api.com/"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApiKey(): String = BuildConfig.HOTELS_API_KEY

    @Provides
    @Singleton
    fun provideOkHttpClient(apiKey: String): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("X-API-KEY", apiKey)
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .registerTypeAdapter(HotelsSearchResponse::class.java, HotelsSearchResponseDeserializer())
        .registerTypeAdapter(Double::class.javaObjectType, doubleFromNumberOrStringAdapter())
        .create()

    private fun doubleFromNumberOrStringAdapter() = JsonDeserializer<Double> { json, _, _ ->
        when {
            json.isJsonPrimitive -> {
                val prim = json.asJsonPrimitive
                if (prim.isNumber) prim.asDouble
                else prim.asString.toDoubleOrNull() ?: 0.0
            }
            else -> 0.0
        }
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    fun provideHotelsApiService(retrofit: Retrofit): HotelsApiService =
        retrofit.create(HotelsApiService::class.java)
}
