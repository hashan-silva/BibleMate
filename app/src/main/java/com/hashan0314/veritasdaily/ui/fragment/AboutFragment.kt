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

package com.hashan0314.veritasdaily.ui.fragment;

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hashan0314.veritasdaily.R
import com.hashan0314.veritasdaily.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?):View {
        _binding = FragmentAboutBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupVersionInfo()
        setupPrivacyPolicyLink()
    }

    private fun setupPrivacyPolicyLink() {
        binding.textViewPrivacyPolicy.setOnClickListener {
            val privacyPolicyUrl = getString(R.string.privacy_policy_url)
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl))
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    private fun setupVersionInfo() {
        try {
            val context = requireContext()
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
            val versionName = packageInfo.versionName
            val verrsionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                packageInfo.versionCode.toLong()
            }
            binding.textViewVersionAbout.text =
                getString(R.string.version_format, packageInfo.versionName, verrsionCode)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            binding.textViewVersionAbout.text = getString(R.string.version_na)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
