package com.magenta.rx.kotlin.concurrent

import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class RXThreadPool private constructor() {

    private object Holder {
        val INSTANCE = RXThreadPool()
    }

    companion object {
        val instance: RXThreadPool by lazy { Holder.INSTANCE }
    }

    private val deque = LinkedBlockingDeque<Runnable>()
    private val executor = ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(), 5, TimeUnit.SECONDS, deque)

    fun put(runnable: Runnable) {
        executor.execute(runnable)
    }
}