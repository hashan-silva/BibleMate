package com.hashan0314.biblemate.network

import com.hashan0314.biblemate.model.RssFeed
import retrofit2.http.GET

interface RssService {
    @GET("en/word-of-the-day.rss.xml")
    suspend fun getDailyGospel(): RssFeed
}