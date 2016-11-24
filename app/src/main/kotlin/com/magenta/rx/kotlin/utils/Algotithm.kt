package com.magenta.rx.kotlin.utils

import com.magenta.rx.kotlin.record.RowResult
import java.util.*

fun calc(start: Int, end: Int, step: Int): List<RowResult> {
    val result = LinkedList<RowResult>()
    var i = start
    while (i <= end) {
        result.add(calc(i))
        i += step
    }
    return result
}

fun calc(x: Int): RowResult {
    Thread.sleep(Random().nextInt(9999).toLong())
    return RowResult(x, System.currentTimeMillis())
}