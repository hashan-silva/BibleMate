package com.hashan0314.veritasdaily

import com.hashan0314.veritasdaily.model.Item
import com.hashan0314.veritasdaily.ui.adapter.GospelDiffCallback
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GospelDiffCallbackTest {
    private val callback = GospelDiffCallback()

    @Test
    fun itemsWithSameGuidAreTreatedAsSame() {
        val oldItem = Item(description = "a", guid = "1", pubDate = "date1")
        val newItem = Item(description = "b", guid = "1", pubDate = "date2")
        assertTrue(callback.areItemsTheSame(oldItem, newItem))
    }

    @Test
    fun itemsWithDifferentGuidAreNotSame() {
        val oldItem = Item(guid = "1")
        val newItem = Item(guid = "2")
        assertFalse(callback.areItemsTheSame(oldItem, newItem))
    }

    @Test
    fun identicalItemsAreRecognizedAsSameContent() {
        val oldItem = Item(description = "desc", guid = "1", pubDate = "date")
        val newItem = Item(description = "desc", guid = "1", pubDate = "date")
        assertTrue(callback.areContentsTheSame(oldItem, newItem))
    }

    @Test
    fun itemsWithDifferentFieldsAreNotSameContent() {
        val oldItem = Item(description = "desc", guid = "1", pubDate = "date")
        val newItem = Item(description = "other", guid = "1", pubDate = "date")
        assertFalse(callback.areContentsTheSame(oldItem, newItem))
    }
}
