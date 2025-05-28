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

package com.hashan0314.biblemate.model

import android.icu.text.CaseMap.Title
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