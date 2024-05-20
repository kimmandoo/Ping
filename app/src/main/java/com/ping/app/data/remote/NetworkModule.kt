package com.ping.app.data.remote

import com.ping.app.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    private const val BASE_URL = "https://api.openai.com/"
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }
    
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val bearer = "Bearer ${BuildConfig.CHAT_GPT}"
            val request = chain.request().newBuilder()
                .addHeader("Authorization", bearer)
                .build()
            chain.proceed(request)
        }
        .build()
    
    val api: ChatGPTService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChatGPTService::class.java)
    }
}