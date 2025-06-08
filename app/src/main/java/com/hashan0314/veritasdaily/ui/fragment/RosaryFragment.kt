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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hashan0314.veritasdaily.databinding.FragmentRosaryBinding
import com.hashan0314.veritasdaily.ui.adapter.RosaryAdapter
import com.hashan0314.veritasdaily.ui.viewmodel.RosaryViewModel
import com.hashan0314.veritasdaily.ui.viewmodel.RosaryViewModelFactory

class RosaryFragment : Fragment() {

    private var _binding: FragmentRosaryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RosaryViewModel by viewModels {
        RosaryViewModelFactory(requireContext())
    }

    private lateinit var rosaryAdapter: RosaryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRosaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (_binding != null) {
                binding.progressBarRosaryFragment.visibility =
                    if (isLoading) View.VISIBLE else View.GONE
            }

        }
        viewModel.displayableItems.observe(viewLifecycleOwner) { items ->
            rosaryAdapter.submitList(items)
            if (items.isEmpty() && viewModel.isLoading.value == false) {
                rosaryAdapter.submitList(emptyList())
            }

        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Log.d("RosaryFragment", "errorMessage LiveData changed: $it")
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearErrorMessage()
            }
        }
        viewModel.rosaryDataFull.observe(viewLifecycleOwner) { fullRosaryData ->
            fullRosaryData?.prayers?.let {
                Log.d("RosaryFragment", "rosaryDataFull LiveData changed: $it")
            }
        }
    }

    private fun setupRecyclerView() {
        rosaryAdapter = RosaryAdapter { headerId ->
            viewModel.toggleMysterySetExpansion(headerId)
        }
        binding.recyclerViewRosary.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = rosaryAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}