package com.example.animalcrossingserotoningenerator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animalcrossingserotoningenerator.api.QuoteApi
import com.example.animalcrossingserotoningenerator.api.QuoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuoteViewModel: ViewModel() {
    private val quoteApi = QuoteApi.create()
    private val quoteRepository = QuoteRepository(quoteApi)
    private val quote = MutableLiveData<String>()
    fun netFetchQuote() = viewModelScope.launch(
        context = viewModelScope.coroutineContext
                + Dispatchers.IO) {
        // Update LiveData from IO dispatcher, use postValue
        quote.postValue(quoteRepository.fetchQuote())
    }

    fun observeQuote(): LiveData<String> {
        return quote
    }

}