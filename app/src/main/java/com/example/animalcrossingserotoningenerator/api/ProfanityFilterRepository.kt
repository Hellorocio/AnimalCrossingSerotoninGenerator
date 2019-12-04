package com.example.animalcrossingserotoningenerator.api

class ProfanityFilterRepository(private val profanityFilterApi: ProfanityFilterApi) {

    suspend fun filterText(unfilteredText: String): String? {
        var response = profanityFilterApi.filterText(unfilteredText)
        return response.result
    }

}