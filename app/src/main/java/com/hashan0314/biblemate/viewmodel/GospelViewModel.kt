package com.hashan0314.biblemate.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashan0314.biblemate.model.Item
import com.hashan0314.biblemate.network.RetrofitClient
import kotlinx.coroutines.launch

class GospelViewModel : ViewModel() {
    val gospel = MutableLiveData<Item>()

    fun fetchGospel() {
        viewModelScope.launch {
            try {
                val feed = RetrofitClient.service.getDailyGospel()
                gospel.value = feed.channel.items.firstOrNull()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}