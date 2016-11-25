package com.magenta.rx.java.presenter;

import android.util.Log;

import com.magenta.rx.java.R;
import com.magenta.rx.kotlin.concurrent.RXThreadPool;
import com.magenta.rx.kotlin.event.CalcEvent;
import com.magenta.rx.kotlin.event.CleanEvent;
import com.magenta.rx.kotlin.event.LockEvent;
import com.magenta.rx.kotlin.event.UnlockEvent;
import com.magenta.rx.kotlin.loader.ConcurrentLoader;
import com.magenta.rx.kotlin.record.ConcurrentConfig;
import com.magenta.rx.kotlin.record.LazyConfig;
import com.magenta.rx.kotlin.record.RowResult;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class ConcurrentPresenter {

    private final RXThreadPool pool;
    private final LazyConfig lazyConfig;
    private final ConcurrentLoader loader;
    private final ConcurrentConfig concurrentConfig;
    private int operationsCount = 0;

    @Inject
    public ConcurrentPresenter(ConcurrentConfig concurrentConfig, LazyConfig lazyConfig, ConcurrentLoader loader) {
        this.loader = loader;
        this.lazyConfig = lazyConfig;
        this.concurrentConfig = concurrentConfig;
        this.pool = RXThreadPool.Companion.getInstance();
    }

    public void start(boolean multithreading, int progress) {
        if (multithreading && progress > 1) {
            double count = (concurrentConfig.getEnd() - concurrentConfig.getStart()) / concurrentConfig.getStep();
            int coreCount = pool.getCount();
            if (count > 0) {
                EventBus.getDefault().postSticky(new LockEvent());
            }
            if ((Math.min(progress, coreCount) >= count) || (progress >= coreCount && progress >= count)) {
                operationsCount = (int) count;
                for (int i = concurrentConfig.getStart(); i <= concurrentConfig.getEnd(); i += concurrentConfig.getStep()) {
                    final int finalI = i;
                    pool.put(new Runnable() {
                        public void run() {
                            EventBus.getDefault().postSticky(new CalcEvent(loader.calc(finalI), false, Thread.currentThread().getName()));
                            if (--operationsCount <= 0) {
                                EventBus.getDefault().postSticky(new UnlockEvent());
                            }
                        }
                    });
                }
            } else {
                for (int i = 0; i < coreCount; i++) {
                    final int x = concurrentConfig.getStart() + concurrentConfig.getStep() * i;
                    pool.put(new Runnable() {
                        public void run() {
                            EventBus.getDefault().postSticky(new CalcEvent(loader.calc(x), true, Thread.currentThread().getName()));
                        }
                    });
                }
                lazyConfig.setMax(concurrentConfig.getEnd());
                lazyConfig.setStep(concurrentConfig.getStep());
                lazyConfig.setCurrent(concurrentConfig.getStart() + concurrentConfig.getStep() * coreCount);
            }
        } else {
            pool.put(new Runnable() {
                public void run() {
                    EventBus.getDefault().postSticky(new LockEvent());
                    for (RowResult result : loader.calc(concurrentConfig.getStart(), concurrentConfig.getEnd(), concurrentConfig.getStep())) {
                        EventBus.getDefault().postSticky(new CalcEvent(result, false, Thread.currentThread().getName()));
                    }
                    EventBus.getDefault().postSticky(new UnlockEvent());
                }
            });
        }
    }

    public void change(int id, CharSequence charSequence) {
        if (charSequence != null && charSequence.length() > 0) {
            try {
                switch (id) {
                    case R.id.start_x: {
                        concurrentConfig.setStart(Integer.valueOf(String.valueOf(charSequence)));
                        break;
                    }
                    case R.id.end_x: {
                        concurrentConfig.setEnd(Integer.valueOf(String.valueOf(charSequence)));
                        break;
                    }
                    case R.id.step: {
                        concurrentConfig.setStep(Integer.valueOf(String.valueOf(charSequence)));
                        break;
                    }
                }
            } catch (Exception e) {
                Log.e(getClass().getName(), e.getMessage(), e);
            }
        }
    }

    public void clean() {
        EventBus.getDefault().postSticky(new CleanEvent());
    }

    public synchronized void continueCalc() {
        if (lazyConfig.getMax() > lazyConfig.getCurrent()) {
            lazyConfig.setCurrent(lazyConfig.getCurrent() + lazyConfig.getStep());
            pool.put(new Runnable() {
                public void run() {
                    EventBus.getDefault().postSticky(new CalcEvent(loader.calc(lazyConfig.getCurrent()), true, Thread.currentThread().getName()));
                }
            });
        } else {
            EventBus.getDefault().postSticky(new UnlockEvent());
        }
    }
}