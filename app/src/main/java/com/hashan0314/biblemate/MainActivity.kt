package com.hashan0314.biblemate

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.hashan0314.biblemate.databinding.ActivityMainBinding
import com.hashan0314.biblemate.viewmodel.GospelViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: GospelViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.gospel.observe(this) { item ->
            binding.titleTextView.text = item.pubDate
            binding.descriptionTextView.text = HtmlCompat.fromHtml(item.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }

        viewModel.fetchGospel()
    }
}