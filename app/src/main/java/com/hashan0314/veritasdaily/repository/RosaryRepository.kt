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

package com.hashan0314.veritasdaily.repository

import android.content.Context
import com.google.gson.Gson
import com.hashan0314.veritasdaily.model.RosaryData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStreamReader

class RosaryRepository(private val context: Context) {
    suspend fun getRosaryData(): RosaryData? {
        return withContext(Dispatchers.IO) {
            try {
                context.assets.open("data/rosary.json").use { inputStream ->
                    InputStreamReader(inputStream, "UTF-8").use { reader ->
                        Gson().fromJson(reader, RosaryData::class.java)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}