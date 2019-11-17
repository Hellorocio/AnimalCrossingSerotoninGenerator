package com.example.animalcrossingserotoningenerator.api

import com.google.gson.GsonBuilder
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface QuoteApi {

    @GET("/api/1.0/?method=getQuote&key=457653&format=json&lang=en")
    suspend fun fetchQuote(): Quote

    companion object {
        private fun buildGsonConverterFactory(): GsonConverterFactory {
            val gsonBuilder = GsonBuilder()
            return GsonConverterFactory.create(gsonBuilder.create())
        }

        var httpurl = HttpUrl.Builder()
            .scheme("http")
            .host("api.forismatic.com")
            .build()
        fun create(): QuoteApi = create(httpurl)
        private fun create(httpUrl: HttpUrl): QuoteApi {

            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    this.level = HttpLoggingInterceptor.Level.BASIC
                })
                .build()
            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
                .addConverterFactory(buildGsonConverterFactory())
                .build()
                .create(QuoteApi::class.java)
        }
    }
}