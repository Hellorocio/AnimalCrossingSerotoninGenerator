package com.example.animalcrossingserotoningenerator.api

class QuoteRepository(private val quoteApi: QuoteApi) {

    suspend fun fetchQuote(): String {
        return quoteApi.fetchQuote().quote
    }

}