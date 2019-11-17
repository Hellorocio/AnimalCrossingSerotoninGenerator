package com.example.animalcrossingserotoningenerator.api

import com.google.gson.annotations.SerializedName

data class Quote (
    @SerializedName("quoteText")
    val quote: String,
    @SerializedName("quoteAuthor")
    val author: String,
    @SerializedName("senderName")
    val senderName: String,
    @SerializedName("senderLink")
    val senderLink: String,
    @SerializedName("quoteLink")
    val quoteLink: String
)