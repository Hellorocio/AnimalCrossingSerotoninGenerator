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

interface ProfanityFilterApi {

    @GET("/service/json")
    suspend fun filterText(@Query("text") unfilteredText: String): ProfanityFilter

    companion object {
        private fun buildGsonConverterFactory(): GsonConverterFactory {
            val gsonBuilder = GsonBuilder()
            return GsonConverterFactory.create(gsonBuilder.create())
        }

        var httpurl = HttpUrl.Builder()
            .scheme("http")
            .host("www.purgomalum.com")
            .build()
        fun create(): ProfanityFilterApi = create(httpurl)
        private fun create(httpUrl: HttpUrl): ProfanityFilterApi {

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
                .create(ProfanityFilterApi::class.java)
        }
    }
}