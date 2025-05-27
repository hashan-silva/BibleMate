package com.hashan0314.biblemate.model

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "channel", strict = false)
data class Channel(
    @field:Element(name = "title")
    var title: String = "",

    @field:Element(name = "description")
    var description: String = "",

    @field:ElementList(inline = true, entry = "item")
    var items: MutableList<Item> = ArrayList()
)
