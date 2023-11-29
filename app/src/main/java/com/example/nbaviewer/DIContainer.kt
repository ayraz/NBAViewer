package com.example.nbaviewer

import com.example.nbaviewer.data.NBAApiService
import com.example.nbaviewer.data.NBARepository
import com.example.nbaviewer.data.NBARepositoryImpl
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

/**
 * Dependency Injection container at the application level.
 */
interface DIContainer {
    val nbaRepository: NBARepository
}

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Dependencies are initialized lazily and the same instances are shared across the whole app.
 */
class DIContainerImpl : DIContainer {
    private val baseUrl = "https://www.balldontlie.io/api/v1/"

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .client(client)
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    private val retrofitService: NBAApiService by lazy {
        retrofit.create(NBAApiService::class.java)
    }

    override val nbaRepository: NBARepository by lazy {
        NBARepositoryImpl(retrofitService)
    }
}