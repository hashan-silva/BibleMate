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

package com.hashan0314.veritasdaily.helper

import android.content.Context
import android.content.SharedPreferences
import java.util.Locale
import kotlin.contracts.contract

class LocaleHelper {
    companion object {
        private const val PREFS_NAME = "app_prefs"
        private const val KEY_SELECTED_LANGUAGE = "selected_language"
        private const val DEFAULT_LANGUAGE = "en"

        fun getSelectedLanguage(context: Context): String {
            val prefs: SharedPreferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(KEY_SELECTED_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
        }

        fun persistLanguage(requireContext: Context, code: String) {
            val prefs: SharedPreferences =
                requireContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(KEY_SELECTED_LANGUAGE, code).apply()
        }

        fun updateBaseContextLocale(newBase: Context): Context {
            val languageCode = getSelectedLanguage(newBase)
            val locale = Locale(languageCode)
            Locale.setDefault(locale)

            val configuration = newBase.resources.configuration
            configuration.setLocale(locale)
            configuration.setLayoutDirection(locale)

            return newBase.createConfigurationContext(configuration)
        }
    }

}
