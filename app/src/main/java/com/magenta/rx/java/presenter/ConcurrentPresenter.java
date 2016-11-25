package com.magenta.rx.java.presenter;

import android.util.Log;

import com.magenta.rx.java.R;
import com.magenta.rx.kotlin.concurrent.RXThreadPool;
import com.magenta.rx.kotlin.event.CalcEvent;
import com.magenta.rx.kotlin.event.CleanEvent;
import com.magenta.rx.kotlin.event.LockEvent;
import com.magenta.rx.kotlin.event.UnlockEvent;
import com.magenta.rx.kotlin.record.ConcurrentConfig;
import com.magenta.rx.kotlin.record.LazyCalc;
import com.magenta.rx.kotlin.record.RowResult;
import com.magenta.rx.kotlin.utils.AlgotithmKt;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class ConcurrentPresenter {

    private final RXThreadPool pool;
    private final ConcurrentConfig concurrentConfig;
    private final LazyCalc lazyCalc;

    @Inject
    public ConcurrentPresenter(ConcurrentConfig concurrentConfig) {
        this.pool = RXThreadPool.Companion.getInstance();
        this.concurrentConfig = concurrentConfig;
        this.lazyCalc = new LazyCalc(0, 0, 0);
    }

    public void start(boolean multithreading, int progress) {
        if (multithreading && progress > 1) {
            double count = (concurrentConfig.getEnd() - concurrentConfig.getStart()) / concurrentConfig.getStep();
            int coreCount = pool.getCount();
            if ((Math.min(progress, coreCount) >= count) || (progress >= coreCount && progress >= count)) {
                for (int i = concurrentConfig.getStart(); i <= concurrentConfig.getEnd(); i += concurrentConfig.getStep()) {
                    final int finalI = i;
                    pool.put(new Runnable() {
                        public void run() {
                            EventBus.getDefault().postSticky(new CalcEvent(AlgotithmKt.calc(finalI), false, Thread.currentThread().getName()));
                        }
                    });
                }
            } else {
                EventBus.getDefault().postSticky(new LockEvent());
                for (int i = 0; i < coreCount; i++) {
                    final int x = concurrentConfig.getStart() + concurrentConfig.getStep() * i;
                    pool.put(new Runnable() {
                        public void run() {
                            EventBus.getDefault().postSticky(new CalcEvent(AlgotithmKt.calc(x), true, Thread.currentThread().getName()));
                        }
                    });
                }
                lazyCalc.setMax(concurrentConfig.getEnd());
                lazyCalc.setStep(concurrentConfig.getStep());
                lazyCalc.setCurrent(concurrentConfig.getStart() + concurrentConfig.getStep() * coreCount);
            }
        } else {
            pool.put(new Runnable() {
                public void run() {
                    for (RowResult result : AlgotithmKt.calc(concurrentConfig.getStart(), concurrentConfig.getEnd(), concurrentConfig.getStep())) {
                        EventBus.getDefault().postSticky(new CalcEvent(result, false, Thread.currentThread().getName()));
                    }
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
        if (lazyCalc.getMax() > lazyCalc.getCurrent()) {
            lazyCalc.setCurrent(lazyCalc.getCurrent() + lazyCalc.getStep());
            pool.put(new Runnable() {
                public void run() {
                    EventBus.getDefault().postSticky(new CalcEvent(AlgotithmKt.calc(lazyCalc.getCurrent()), true, Thread.currentThread().getName()));
                }
            });
        } else {
            EventBus.getDefault().postSticky(new UnlockEvent());
        }
    }
}