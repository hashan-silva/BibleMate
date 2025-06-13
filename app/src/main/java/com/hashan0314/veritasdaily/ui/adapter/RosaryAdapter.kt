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

import android.animation.LayoutTransition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.hashan0314.veritasdaily.R
import com.hashan0314.veritasdaily.databinding.ItemMysterySetHeaderBinding
import com.hashan0314.veritasdaily.databinding.ItemRosaryMysteryBinding
import com.hashan0314.veritasdaily.databinding.ItemStandardPrayerBinding
import com.hashan0314.veritasdaily.model.MysterySetHeaderData
import com.hashan0314.veritasdaily.model.RosaryMystery
import com.hashan0314.veritasdaily.model.StandardPrayer

private const val VIEW_TYPE_STANDARD_PRAYER = 1
private const val VIEW_TYPE_ROSARY_MYSTERY = 2
private const val VIEW_TYPE_MYSTERY_SET_HEADER = 3

class RosaryAdapter(private val onHeaderClicked: (String) -> Unit) :
    ListAdapter<Any, ViewHolder>(RosaryDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is StandardPrayer -> VIEW_TYPE_STANDARD_PRAYER
            is RosaryMystery -> VIEW_TYPE_ROSARY_MYSTERY
            is MysterySetHeaderData -> VIEW_TYPE_MYSTERY_SET_HEADER
            else -> throw IllegalArgumentException(
                "Unsupported data type at position $position: ${
                    getItem(
                        position
                    ).javaClass.name
                }"
            )
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_STANDARD_PRAYER -> {
                val binding = ItemStandardPrayerBinding.inflate(inflater, parent, false)
                (binding.layoutPrayerContent.parent as? ViewGroup)?.layoutTransition?.enableTransitionType(
                    LayoutTransition.CHANGING
                )
                StandardPrayerViewHolder(binding)
            }

            VIEW_TYPE_ROSARY_MYSTERY -> {
                val binding = ItemRosaryMysteryBinding.inflate(inflater, parent, false)
                (binding.layoutMysteryContent.parent as? ViewGroup)?.layoutTransition?.enableTransitionType(
                    LayoutTransition.CHANGING
                )
                MysteryViewHolder(binding)
            }

            VIEW_TYPE_MYSTERY_SET_HEADER -> {
                val binding = ItemMysterySetHeaderBinding.inflate(inflater, parent, false)
                MysterySetHeaderViewHolder(binding, onHeaderClicked)
            }

            else -> throw IllegalArgumentException("Unsupported view type: $viewType")
        }
    }

    inner class MysterySetHeaderViewHolder(
        private val binding: ItemMysterySetHeaderBinding,
        onHeaderClicked: (String) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(headerData: MysterySetHeaderData) {
            binding.textViewMysterySetHeaderTitle.text = headerData.setName
            if (headerData.daysDisplay != null && headerData.daysDisplay.isNotBlank()) {
                binding.textViewMysterySetHeaderDays.text = headerData.daysDisplay
                binding.textViewMysterySetHeaderDays.visibility = View.VISIBLE
            } else {
                binding.textViewMysterySetHeaderDays.visibility = View.GONE
            }
            val headerIconResId =
                if (headerData.isExpanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down
            binding.imageViewHeaderExpandIcon.setImageResource(headerIconResId)
            binding.root.setOnClickListener {
                onHeaderClicked(headerData.id)
            }
        }

    }

    inner class MysteryViewHolder(private val binding: ItemRosaryMysteryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(mystery: RosaryMystery) {
            binding.textViewMysteryTitle.text = mystery.title
            binding.textViewMysteryCaption.text = mystery.caption
            binding.textViewMysteryDescription.text = mystery.description
            binding.textViewMysteryFruit.text = "Fruit of the Mystery: ${mystery.fruit}"

            updateContentVisibility(mystery.isExpanded)

            binding.layoutTitleSection.setOnClickListener {
                mystery.isExpanded = !mystery.isExpanded
                updateContentVisibility(mystery.isExpanded)
            }
        }

        private fun updateContentVisibility(isExpanded: Boolean) {
            binding.layoutMysteryContent.visibility = if (isExpanded) View.VISIBLE else View.GONE

            val iconResId = if (isExpanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down
            binding.imageViewExpandIcon.setImageResource(iconResId)
        }

    }

    inner class StandardPrayerViewHolder(private val binding: ItemStandardPrayerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(prayer: StandardPrayer) {
            binding.textViewPrayerTitle.text = prayer.title
            binding.testViewPrayerText.text = prayer.content
            updateContentVisibility(prayer.isExpanded)

            binding.layoutTitleSectionPrayer.setOnClickListener {
                prayer.isExpanded = !prayer.isExpanded
                updateContentVisibility(prayer.isExpanded)
            }
        }

        private fun updateContentVisibility(isExpanded: Boolean) {
            binding.layoutPrayerContent.visibility = if (isExpanded) View.VISIBLE else View.GONE

            val iconResId = if (isExpanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down
            binding.imageViewExpandIconPrayer.setImageResource(iconResId)
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is StandardPrayerViewHolder -> holder.bind(item as StandardPrayer)
            is MysteryViewHolder -> holder.bind(item as RosaryMystery)
            is MysterySetHeaderViewHolder -> holder.bind(item as MysterySetHeaderData)
        }
    }
}