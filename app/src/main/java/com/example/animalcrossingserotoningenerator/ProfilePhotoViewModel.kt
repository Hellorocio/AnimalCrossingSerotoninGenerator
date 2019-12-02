package com.example.animalcrossingserotoningenerator
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.random.Random
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import kotlin.coroutines.coroutineContext


class ProfilePhotoViewModel : ViewModel() {
    private var list = MutableLiveData<List<String>>().apply {
        value = listOf<String>()
    }
    internal var selected = -1


    internal fun getList(): LiveData<List<String>> {
        return list
    }

    fun init(c: Context) {
        // thanks stack overflow https://stackoverflow.com/questions/3221603/retrieving-all-drawable-resources-from-resources-object/3221787
        var picList = mutableListOf<String>()
        for (identifier in R.drawable.ac_agnes + 1..R.drawable.ac_zucker - 1) {
            val name = c.resources.getResourceEntryName(identifier)
            picList.add(name)
        }

        list.value = picList
    }
}