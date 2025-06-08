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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashan0314.veritasdaily.model.MysterySetHeaderData
import com.hashan0314.veritasdaily.model.RosaryData
import com.hashan0314.veritasdaily.repository.RosaryRepository
import kotlinx.coroutines.launch
import java.util.Locale

class RosaryViewModel(private val repository: RosaryRepository) : ViewModel() {

    private val _rosaryDataFull = MutableLiveData<RosaryData?>()
    val rosaryDataFull: LiveData<RosaryData?> = _rosaryDataFull

    private val _displayableItems = MutableLiveData<List<Any>>()
    val displayableItems: LiveData<List<Any>> = _displayableItems

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private var currentRosaryData: RosaryData? = null
    private val mysterySetExpansionStates = mutableMapOf<String, Boolean>()

    init {
        fetchRosaryData()
    }

    fun fetchRosaryData() {
        viewModelScope.launch {
            _isLoading.postValue(true)
            _errorMessage.postValue(null)
            try {
                currentRosaryData = repository.getRosaryData()
                _rosaryDataFull.postValue(currentRosaryData)
                currentRosaryData?.mysterySets?.forEach { mysterySet ->
                    if (!mysterySetExpansionStates.containsKey(mysterySet.setName)) {
                        mysterySetExpansionStates[mysterySet.setName] = false
                    }
                }
                prepareDisplayableItems()
            } catch (e: Exception) {
                _errorMessage.postValue("Error occurred: ${e.message}")
                currentRosaryData = null
                _displayableItems.postValue(emptyList())
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    private fun prepareDisplayableItems() {
        val data = currentRosaryData
        if (data == null) {
            _displayableItems.postValue(emptyList())
            if (!_isLoading.value!!) {
                _errorMessage.postValue("Failed to load Rosary Data")
            }
            return
        }

        val items = mutableListOf<Any>()

        if (data.prayers.isNotEmpty()) {
            items.addAll(data.prayers)
        }

        data.mysterySets.forEach { mysterySet ->
            if (mysterySet.mysteries.isNotEmpty()) {
                val headerId = mysterySet.setName
                val isExpanded = mysterySetExpansionStates[headerId] ?: false
                val daysDisplayString = formatDays(mysterySet.days)
                items.add(
                    MysterySetHeaderData(
                        headerId,
                        mysterySet.setName,
                        daysDisplayString,
                        isExpanded
                    )
                )
                if (isExpanded) {
                    items.addAll(mysterySet.mysteries)
                }
            }
        }
        _displayableItems.postValue(items)
    }

    private fun formatDays(days: List<String>): String? {
        return days.takeIf {
            it.isNotEmpty()
        }?.joinToString(", ") { day ->
            when (day.lowercase(Locale.getDefault())) {
                "mon" -> "Mondays"
                "tue" -> "Tuesdays"
                "wed" -> "Wednesdays"
                "thu" -> "Thursdays"
                "fri" -> "Fridays"
                "sat" -> "Saturdays"
                "sun" -> "Sundays"
                else -> day.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.postValue(null)
    }

    fun toggleMysterySetExpansion(headerId: String) {
        val currentState = mysterySetExpansionStates[headerId] ?: false
        mysterySetExpansionStates[headerId] = !currentState
        prepareDisplayableItems()
    }

}