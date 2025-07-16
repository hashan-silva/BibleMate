package com.hashan0314.veritasdaily.ui.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.hashan0314.veritasdaily.model.Item
import com.hashan0314.veritasdaily.repository.GospelRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class GospelViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var mockRepository: GospelRepository

    @Mock
    private lateinit var mockApplication: Application

    private lateinit var viewModel: GospelViewModel

    private val pubDateFormat = SimpleDateFormat(
        "EEE, dd MMM yyyy HH:mm:ss Z",
        Locale.ENGLISH
    )
    private val testTimeZone: TimeZone = TimeZone.getTimeZone("GMT")

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        pubDateFormat.timeZone = testTimeZone
        viewModel = GospelViewModel(mockApplication, mockRepository)
        viewModel.onInit(TextToSpeech.SUCCESS)
    }

    @After
    fun tearDown(){
        Dispatchers.resetMain()
        viewModel.onCleared()
    }

    @Test
    fun `fetchGospel success updates LiveData and sets initialDataLoaded`() = runTest {
        val mockItems = listOf(
            createMockItem("Mon, 01 Jan 2024 10:00:00 +0000", "Gospel 1"),
            createMockItem("Tue, 02 Jan 2024 11:00:00 +0000", "Gospel 2")
        )
    }

    private fun createMockItem(pubDateString: String, title: String): Item {

    }

}