package com.magenta.rx.kotlin.concurrent

import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class RXThreadPool private constructor(val count: Int, deque: LinkedBlockingDeque<Runnable>) {

    private object Holder {
        val INSTANCE = RXThreadPool(Runtime.getRuntime().availableProcessors(), LinkedBlockingDeque<Runnable>())
    }

    companion object {
        val instance: RXThreadPool by lazy { Holder.INSTANCE }
    }

    private val executor = ThreadPoolExecutor(count, count, 5, TimeUnit.SECONDS, deque)

    fun put(runnable: Runnable) {
        executor.execute(runnable)
    }
}