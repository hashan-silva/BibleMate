package com.hashan0314.biblemate.model

import org.simpleframework.xml.Element
import org.simpleframework.xml.Namespace
import org.simpleframework.xml.Root

@Root(name = "item", strict = false)
data class Item(

    @field:Element(name = "description", required = false)
    var description: String = "",

    @field:Element(name = "guid", required = false)
    var guid: String = "",

    @field:Element(name = "pubDate", required = false)
    var pubDate: String = ""
)