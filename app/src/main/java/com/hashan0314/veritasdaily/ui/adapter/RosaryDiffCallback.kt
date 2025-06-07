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

package com.hashan0314.veritasdaily.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.hashan0314.veritasdaily.model.MysterySetHeaderData
import com.hashan0314.veritasdaily.model.RosaryMystery
import com.hashan0314.veritasdaily.model.StandardPrayer

class RosaryDiffCallback : DiffUtil.ItemCallback<Any>() {

    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        return when {
            oldItem is StandardPrayer && newItem is StandardPrayer -> oldItem.id == newItem.id
            oldItem is RosaryMystery && newItem is RosaryMystery -> oldItem.id == newItem.id
            oldItem is MysterySetHeaderData && newItem is MysterySetHeaderData -> oldItem.id == newItem.id
            else -> false

        }
    }

    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        return when {
            oldItem is StandardPrayer && newItem is StandardPrayer -> oldItem == newItem
            oldItem is RosaryMystery && newItem is RosaryMystery -> oldItem == newItem
            oldItem is MysterySetHeaderData && newItem is MysterySetHeaderData -> oldItem == newItem
            else -> oldItem == newItem
        }
    }

}
