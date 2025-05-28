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

package com.hashan0314.biblemate

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.hashan0314.biblemate.databinding.ActivityMainBinding
import com.hashan0314.biblemate.repository.GospelRepository
import com.hashan0314.biblemate.viewmodel.GospelAdapter
import com.hashan0314.biblemate.viewmodel.GospelViewModel
import com.hashan0314.biblemate.viewmodel.GospelViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var gospelAdapter: GospelAdapter

    private val viewModel: GospelViewModel by viewModels {
        GospelViewModelFactory(GospelRepository())
    }

    private var uiInitialized = false

    private val tabDates = mutableListOf<Date>()
    private val tabDateFormat = SimpleDateFormat("MMM d", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        gospelAdapter = GospelAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = gospelAdapter
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (!isLoading) {
                binding.appBarLayout.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.VISIBLE
                initializeUiAfterDataLoad()
            } else {
                binding.appBarLayout.visibility = View.GONE
                binding.recyclerView.visibility = View.GONE
            }
        }

        viewModel.filteredGospelList.observe(this) { items ->
            gospelAdapter.submitList(items)
        }

        viewModel.currentSelectedDateLiveData.observe(this) { selectedDate ->
            selectedDate?.let { dateToSelect ->
                if (uiInitialized) {
                    val position = tabDates.indexOfFirst {
                        val cal1 = Calendar.getInstance().apply { time = it }
                        val cal2 = Calendar.getInstance().apply { time = dateToSelect }
                        cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) && cal1.get(
                            Calendar.YEAR
                        ) == cal2.get(Calendar.YEAR)
                    }
                    if (position != -1 && binding.tabLayout.selectedTabPosition != position) {
                        binding.tabLayout.getTabAt(position)?.select()
                    }
                }
            }
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                // You could also show this message in a TextView on the screen
            }
        }

    }

    private fun initializeUiAfterDataLoad() {
        if (!uiInitialized) {
            setupDateTabs()
            if (binding.tabLayout.tabCount > 0) {
                binding.tabLayout.getTabAt(0)?.select()
            }
            uiInitialized = true
        }
    }

    private fun setupDateTabs() {
        tabDates.clear()
        binding.tabLayout.removeAllTabs()
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        for (i in 0 until 7) {
            val dateForTab = calendar.time
            tabDates.add(dateForTab)
            binding.tabLayout.addTab(
                binding.tabLayout.newTab().setText(tabDateFormat.format(dateForTab))
            )
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    if (it.position < tabDates.size) {
                        val selectedDate = tabDates[it.position]
                        viewModel.filterBySpecificDate(selectedDate)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                tab?.let {
                    if (it.position < tabDates.size) {
                        val selectedDate = tabDates[it.position]
                        viewModel.filterBySpecificDate(selectedDate)
                    }
                }
            }

        })
    }
}