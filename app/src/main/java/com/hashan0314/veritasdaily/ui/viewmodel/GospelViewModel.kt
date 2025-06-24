/*
 * Copyright (c) 2025. Hashan Silva
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 */

package com.hashan0314.veritasdaily.ui.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hashan0314.veritasdaily.model.Item
import com.hashan0314.veritasdaily.repository.GospelRepository
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class GospelViewModel(application: Application, private val repository: GospelRepository) :
    AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val _currentSelectedDate = MutableLiveData<Date?>()
    val currentSelectedDateLiveData: LiveData<Date?> = _currentSelectedDate;
    private val _originalGospelList = MutableLiveData<List<Item>>()
    val originalGospelList: LiveData<List<Item>> = _originalGospelList
    private val _filteredGospelList = MutableLiveData<List<Item>>()
    val filteredGospelList: LiveData<List<Item>> = _filteredGospelList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isInitialDataLoaded = MutableLiveData<Boolean>(false)
    val isInitialDataLoaded: LiveData<Boolean> = _isInitialDataLoaded

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private var tts: TextToSpeech? = null
    private val _isTtsInitialized = MutableLiveData<Boolean>(false)
    val isTtsInitialized: LiveData<Boolean> = _isTtsInitialized

    private val _isSpeaking = MutableLiveData<Boolean>(false)
    val isSpeaking: LiveData<Boolean> = _isSpeaking

    private val pubDateFormat = SimpleDateFormat(
        "EEE, dd MMM yyyy HH:mm:ss Z",
        Locale.ENGLISH
    )

    init {
        fetchGospel()
        tts = TextToSpeech(application, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.getDefault())
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("GospelViewModel", "Language not supported")
                _errorMessage.postValue("TTS language not supported")
                _isTtsInitialized.postValue(false)
            } else {
                _isTtsInitialized.postValue(true)
                Log.i("GospelViewModel", "TTS initialized")

            }
        } else {
            Log.e("GospelViewModel", "TTS initialization failed")
            _errorMessage.postValue("TTS initialization failed")
            _isTtsInitialized.postValue(false)
        }
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _isSpeaking.postValue(true)
            }

            override fun onDone(utteranceId: String?) {
                _isSpeaking.postValue(false)
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _isSpeaking.postValue(false)
                _errorMessage.postValue("TTS error occurred")
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                _isSpeaking.postValue(false)
                _errorMessage.postValue("TTS error occurred: $utteranceId, error code: $errorCode")
                Log.e("GospelViewModel", "TTS error occurred: $utteranceId, error code: $errorCode")
            }

        })
    }

    fun speakGospel(text: String) {
        if (_isTtsInitialized.value == true && tts != null) {
            if (text.isNotBlank()) {
                val utteranceId = "gospel_utterence_${System.currentTimeMillis()}"
                Log.d("GospelViewModel_TTS", "Attempting to speak: '$text' with id: $utteranceId")
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
            }else{
                Log.w("GospelViewModel_TTS", "Empty text to speak")
                _errorMessage.postValue("Empty text to speak")
            }
        }else{
            Log.e("GospelViewModel_TTS", "TTS not initialized")
            _errorMessage.postValue("TTS not initialized")
            if(tts == null){
                Log.d("GospelViewModel_TTS", "TTS is null")
                tts = TextToSpeech(getApplication(), this)
            }
        }
    }

    fun stopSpeaking() {
        if(tts?.isSpeaking == true){
            tts?.stop()
            Log.d("GospelViewModel_TTS", "TTS stopped")
        }
        _isSpeaking.postValue(false)
    }

    override fun onCleared() {
        super.onCleared()
        if(tts != null){
            tts?.stop()
            tts?.shutdown()
            tts = null
            Log.i("GospelViewModel_TTS", "TTS shutdown")
        }
    }

    fun fetchGospel() {// Start loading
        _errorMessage.postValue(null)
        viewModelScope.launch {
            _isInitialDataLoaded.postValue(false)
            _isLoading.postValue(true)
            try {
                val feed = repository.fetchRssFeed()
                val items = feed.channel.items
                _originalGospelList.postValue(items)
                _isInitialDataLoaded.postValue(true)
                applyFilterForSelectedDate(_currentSelectedDate.value)
            } catch (e: Exception) {
                Log.e("GospelViewModel", "Error fetching gospel data" + e.stackTraceToString(), e)
                _originalGospelList.postValue(emptyList())
                _filteredGospelList.postValue(emptyList())
                _isInitialDataLoaded.postValue(true)
                _errorMessage.postValue("Error fetching data: ${e.localizedMessage}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    private fun applyFilterForSelectedDate(selectedDate: Date? = _currentSelectedDate.value) {
        val originalList = _originalGospelList.value ?: emptyList()
        if (selectedDate == null) {
            _filteredGospelList.postValue(originalList)
            return
        }
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.time = selectedDate
        val startOfDay = getStartOfDay(calendar.clone() as Calendar).time
        val endOfDate = getEndOfDay(calendar.clone() as Calendar).time

        val filteredList = originalList.filter { item ->
            try {
                val itemPubDateString = item.pubDate
                if (itemPubDateString.isNotBlank()) {
                    val itemPubDate = pubDateFormat.parse(itemPubDateString)
                    itemPubDate?.let {
                        it.time >= startOfDay.time && it.time <= endOfDate.time
                    } ?: false
                } else {
                    false
                }
            } catch (e: ParseException) {
                Log.w(
                    "GospelViewModel",
                    "Could not parse date: '${item.pubDate}' for item: '${item.pubDate}'",
                    e
                )
                false
            } catch (e: Exception) {
                Log.e(
                    "GospelViewModel",
                    "Error filtering item: '${item.pubDate}' by specific date",
                    e
                )
                false
            }
        }
        _filteredGospelList.postValue(filteredList)
    }

    private fun getEndOfDay(calendar: Calendar): Calendar {
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 99)
        return calendar
    }

    private fun getStartOfDay(calendar: Calendar): Calendar {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar
    }

    fun filterBySpecificDate(date: Date) {
        _currentSelectedDate.postValue(date)
        applyFilterForSelectedDate(date)
    }
}