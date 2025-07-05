package com.hashan0314.veritasdaily

import com.hashan0314.veritasdaily.model.MysterySetHeaderData
import com.hashan0314.veritasdaily.model.RosaryMystery
import com.hashan0314.veritasdaily.model.StandardPrayer
import com.hashan0314.veritasdaily.ui.adapter.RosaryDiffCallback
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RosaryDiffCallbackTest {
    private val callback = RosaryDiffCallback()

    @Test
    fun standardPrayersWithSameIdAreSameItem() {
        val old = StandardPrayer("id","title","text")
        val new = StandardPrayer("id","other","text2")
        assertTrue(callback.areItemsTheSame(old, new))
    }

    @Test
    fun rosaryMysteriesWithSameIdAreSameItem() {
        val old = RosaryMystery("id","title","cap","desc","fruit")
        val new = RosaryMystery("id","t","c","d","fruit")
        assertTrue(callback.areItemsTheSame(old, new))
    }

    @Test
    fun differentTypesAreNotTheSame() {
        val prayer = StandardPrayer("id","title","content")
        val mystery = RosaryMystery("other","t","c","d","f")
        assertFalse(callback.areItemsTheSame(prayer, mystery))
    }

    @Test
    fun identicalHeaderDataAreSameContent() {
        val old = MysterySetHeaderData("h1","Glorious","Mondays", true)
        val new = MysterySetHeaderData("h1","Glorious","Mondays", true)
        assertTrue(callback.areContentsTheSame(old, new))
    }

    @Test
    fun headerDataWithDifferentExpansionStateAreNotSameContent() {
        val old = MysterySetHeaderData("h1","Glorious","Mondays", true)
        val new = MysterySetHeaderData("h1","Glorious","Mondays", false)
        assertFalse(callback.areContentsTheSame(old, new))
    }
}
