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

package com.hashan0314.veritasdaily.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.hashan0314.veritasdaily.databinding.FragementDailyGospelBinding
import com.hashan0314.veritasdaily.viewmodel.GospelAdapter
import com.hashan0314.veritasdaily.viewmodel.GospelViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DailyGospelFragment : Fragment() {
    private var _binding: FragementDailyGospelBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GospelViewModel by activityViewModels()

    private lateinit var gospelAdapter: GospelAdapter
    private val tabDates = mutableListOf<Date>()
    private val tabDateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
    private var uiInitialized = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragementDailyGospelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupDateTabs()
        if (!uiInitialized && viewModel.isLoading.value == false) {
            // If not loading and UI not init, proceed
            initializeUiAfterDataLoad()
        }
    }

    private fun initializeUiAfterDataLoad() {
        if (!uiInitialized) {
            if (binding.tabLayoutGospel.tabCount > 0 && binding.tabLayoutGospel.selectedTabPosition == -1) {
                // Select the first tab if no tab is currently selected.
                // This will trigger onTabSelected, which calls viewModel.filterBySpecificDate().
                binding.tabLayoutGospel.getTabAt(0)?.select()
            }
            uiInitialized = true
        }
    }

    private fun setupDateTabs() {
        if (uiInitialized) {
            return
        }
        tabDates.clear()
        binding.tabLayoutGospel.removeAllTabs()
        val calendar = Calendar.getInstance(TimeZone.getDefault())

        for (i in 0 until 7) {
            val dateForTab = calendar.time
            tabDates.add(dateForTab)
            binding.tabLayoutGospel.addTab(
                binding.tabLayoutGospel.newTab().setText(tabDateFormat.format(dateForTab))
            )
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        binding.tabLayoutGospel.clearOnTabSelectedListeners()
        binding.tabLayoutGospel.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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

    private fun setupObservers() {
        viewModel.filteredGospelList.observe(viewLifecycleOwner) { items ->
            gospelAdapter.submitList(items)
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (!isLoading && !uiInitialized) {
                setupDateTabs()
                initializeUiAfterDataLoad()
            }
        }
        viewModel.currentSelectedDateLiveData.observe(viewLifecycleOwner) { selectedDate ->
            selectedDate?.let { dateToSelect ->
                if (uiInitialized) {
                    val position = tabDates.indexOfFirst {
                        val cal1 = Calendar.getInstance().apply { time = it }
                        val cal2 = Calendar.getInstance().apply { time = dateToSelect }
                        cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) && cal1.get(
                            Calendar.YEAR
                        ) == cal2.get(Calendar.YEAR)
                    }
                    if (position != -1 && binding.tabLayoutGospel.selectedTabPosition != position) {
                        binding.tabLayoutGospel.getTabAt(position)?.select()
                    }
                }
            }
        }
        viewModel.isInitialDataLoaded.observe(viewLifecycleOwner){
            isLoaded ->
            if(isLoaded && !uiInitialized){
                if (!viewModel.originalGospelList.value.isNullOrEmpty()) {
                    binding.tabLayoutGospel.visibility = View.VISIBLE
                    setupDateTabs()
                }else{
                    binding.tabLayoutGospel.visibility = View.GONE
                    uiInitialized = false
                }
            }else if(!isLoaded){
                binding.tabLayoutGospel.visibility = View.GONE
                uiInitialized = false
                tabDates.clear()
                binding.tabLayoutGospel.removeAllTabs()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        uiInitialized = false
        _binding = null
    }

    private fun setupRecyclerView() {
        gospelAdapter = GospelAdapter()
        binding.recyclerViewGospel.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = gospelAdapter
        }
    }
}