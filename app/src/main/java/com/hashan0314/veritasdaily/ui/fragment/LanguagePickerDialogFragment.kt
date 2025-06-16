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

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.hashan0314.veritasdaily.R
import com.hashan0314.veritasdaily.databinding.DialogLanguagePickerBinding
import com.hashan0314.veritasdaily.helper.LocaleHelper
import com.hashan0314.veritasdaily.listener.LanguageDialogListener
import com.hashan0314.veritasdaily.model.LanguageItem

class LanguagePickerDialogFragment : DialogFragment() {

    private var _binding: DialogLanguagePickerBinding? = null
    private val binding get() = _binding!!

    private var listener: LanguageDialogListener? = null
    private var selectedLanguageCode: String? = null

    companion object {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogLanguagePickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val availableLanguages = listOf(
            LanguageItem(getString(R.string.language_english), "en"),
            LanguageItem(getString(R.string.language_italian), "it")
        )

        val currentStoredLanguageCode = LocaleHelper.getSelectedLanguage(requireContext())
        selectedLanguageCode = currentStoredLanguageCode

        val adapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                availableLanguages
            )

        val autoCompleteTextView = binding.autoCompleteTextViewLanguages

        autoCompleteTextView.setAdapter(adapter)

        val currentLanguageItem = availableLanguages.find { it.code == currentStoredLanguageCode }

        autoCompleteTextView.setText(currentLanguageItem?.displayName ?: "", false)

        autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as LanguageItem
            selectedLanguageCode = selectedItem.code
        }

        binding.buttonCancelLanguage.setOnClickListener { dismiss() }

        binding.buttonApplyLanguage.setOnClickListener {
            selectedLanguageCode?.let { code ->
                if (code != currentStoredLanguageCode) {
                    LocaleHelper.persistLanguage(requireContext(), code)
                    listener?.onLanguageSelectedAndApplied()
                }
            }
            dismiss()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LanguageDialogListener) {
            listener = context
        } else if (parentFragment is LanguageDialogListener) {
            listener = parentFragment as LanguageDialogListener
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


}