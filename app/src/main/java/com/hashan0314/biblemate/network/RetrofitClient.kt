package com.hashan0314.biblemate.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

object RetrofitClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.vaticannews.va/")
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .client(OkHttpClient.Builder().build())
        .build()

    val service: RssService = retrofit.create(RssService::class.java)
}