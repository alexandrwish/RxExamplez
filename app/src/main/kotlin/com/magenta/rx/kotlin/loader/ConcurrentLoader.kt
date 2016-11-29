package com.magenta.rx.kotlin.loader

import com.magenta.rx.kotlin.record.RowResult
import java.util.*
import javax.inject.Inject

class ConcurrentLoader @Inject constructor() {

    fun calc(start: Int, end: Int, step: Int): List<RowResult> {
        val result = LinkedList<RowResult>()
        (start..end step step).forEach { i -> result.add(calc(i)) }
        return result
    }

    fun calc(x: Int): RowResult {
        Thread.sleep(Random().nextInt(9999).toLong())
        return RowResult(x, System.currentTimeMillis())
    }
}