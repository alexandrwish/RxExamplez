package com.magenta.rx.kotlin.utils

import java.util.*

fun sortDictionaryStrings(massive: List<String>) = massive.sortedWith(Comparator { o1, o2 ->
    if (o1.equals("text", true)) -1 else
        if (o2.equals("text", true)) 1 else
            if (o1.equals("pos", true)) -1 else
                if (o2.equals("pos", true)) 1 else 0
})