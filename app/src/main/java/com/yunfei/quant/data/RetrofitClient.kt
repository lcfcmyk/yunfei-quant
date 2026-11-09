package com.yunfei.quant.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    @Volatile private var api: ApiService? = null
    @Volatile private var baseUrl: String = ""

    fun get(base: String): ApiService {
        val normalized = if (base.endsWith("/")) base else "$base/"
        api?.let { if (normalized == baseUrl) return it }
        synchronized(this) {
            api?.let { if (normalized == baseUrl) return it }
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
            val client = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()
            baseUrl = normalized
            val service = Retrofit.Builder()
                .baseUrl(normalized)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
            api = service
            return service
        }
    }
}
