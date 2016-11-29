package com.magenta.rx.kotlin.presenter

import android.util.Log
import com.magenta.rx.java.R
import com.magenta.rx.kotlin.concurrent.RXThreadPool
import com.magenta.rx.kotlin.event.CalcEvent
import com.magenta.rx.kotlin.event.CleanEvent
import com.magenta.rx.kotlin.event.LockEvent
import com.magenta.rx.kotlin.event.UnlockEvent
import com.magenta.rx.kotlin.loader.ConcurrentLoader
import com.magenta.rx.kotlin.record.ConcurrentConfig
import com.magenta.rx.kotlin.record.LazyConfig
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class ConcurrentPresenter @Inject constructor(private val concurrentConfig: ConcurrentConfig, private val lazyConfig: LazyConfig, private val loader: ConcurrentLoader) {

    private val pool: RXThreadPool
    private var operationsCount = 0

    init {
        this.pool = RXThreadPool.instance
    }

    fun start(multithreading: Boolean, progress: Int) {
        if (multithreading && progress > 1) {
            val count = ((concurrentConfig.end - concurrentConfig.start) / concurrentConfig.step).toDouble()
            val coreCount = pool.count
            if (count > 0) {
                EventBus.getDefault().postSticky(LockEvent())
                if (Math.min(progress, coreCount) >= count || progress >= coreCount && progress >= count) {
                    operationsCount = count.toInt()
                    (concurrentConfig.start..concurrentConfig.end step concurrentConfig.step).forEach {
                        pool.put(Runnable {
                            EventBus.getDefault().postSticky(CalcEvent(loader.calc(it), false, Thread.currentThread().name))
                            if (--operationsCount <= 0) {
                                EventBus.getDefault().postSticky(UnlockEvent())
                            }
                        })
                    }
                } else {
                    (0..coreCount - 1)
                            .map { concurrentConfig.start + concurrentConfig.step * it }
                            .forEach { pool.put(Runnable { EventBus.getDefault().postSticky(CalcEvent(loader.calc(it), true, Thread.currentThread().name)) }) }
                    lazyConfig.max = concurrentConfig.end
                    lazyConfig.step = concurrentConfig.step
                    lazyConfig.current = concurrentConfig.start + concurrentConfig.step * coreCount
                }
            }
        } else {
            pool.put(Runnable {
                EventBus.getDefault().postSticky(LockEvent())
                loader.calc(concurrentConfig.start, concurrentConfig.end, concurrentConfig.step).forEach { EventBus.getDefault().postSticky(CalcEvent(it, false, Thread.currentThread().name)) }
                EventBus.getDefault().postSticky(UnlockEvent())
            })
        }
    }

    fun change(id: Int, charSequence: CharSequence?) {
        if (charSequence != null && charSequence.isNotEmpty()) {
            try {
                when (id) {
                    R.id.start_x -> {
                        concurrentConfig.start = Integer.valueOf(charSequence.toString())
                    }
                    R.id.end_x -> {
                        concurrentConfig.end = Integer.valueOf(charSequence.toString())
                    }
                    R.id.step -> {
                        concurrentConfig.step = Integer.valueOf(charSequence.toString())
                    }
                }
            } catch (e: Exception) {
                Log.e(javaClass.name, e.message, e)
            }

        }
    }

    fun clean() {
        EventBus.getDefault().postSticky(CleanEvent())
    }

    @Synchronized fun continueCalc() {
        if (lazyConfig.max >= lazyConfig.current) {
            lazyConfig.current = lazyConfig.current + lazyConfig.step
            pool.put(Runnable { EventBus.getDefault().postSticky(CalcEvent(loader.calc(lazyConfig.current), true, Thread.currentThread().name)) })
        } else {
            EventBus.getDefault().postSticky(UnlockEvent())
        }
    }
}