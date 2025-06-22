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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hashan0314.veritasdaily.databinding.ItemGospelBinding
import com.hashan0314.veritasdaily.model.Item
import java.text.SimpleDateFormat
import java.util.Locale

class GospelAdapter(
    private val onItemClick: ((Item) -> Unit)? = null
) : ListAdapter<Item, GospelAdapter.GospelViewHolder>(GospelDiffCallback()) {

    inner class GospelViewHolder(private val binding: ItemGospelBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            if (item.pubDate.isNotBlank()) {
                try {
                    val date =
                        SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.getDefault()).parse(
                            item.pubDate
                        )
                    if (date != null) {
                        val formattedDate =
                            SimpleDateFormat("EEE dd MMM yyyy", Locale.getDefault()).format(date)
                        binding.itemTitle.text = formattedDate
                    } else {
                        binding.itemTitle.text = item.pubDate
                    }
                } catch (e: Exception) {
                    binding.itemTitle.text = item.pubDate
                }
            } else {
                binding.itemTitle.text = item.pubDate
            }
            binding.itemDescription.text =
                HtmlCompat.fromHtml(item.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
            binding.root.setOnClickListener {onItemClick?.invoke(item)}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GospelViewHolder {
        val binding = ItemGospelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GospelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GospelViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

}