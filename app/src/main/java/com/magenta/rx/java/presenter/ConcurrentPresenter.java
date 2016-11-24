package com.magenta.rx.java.presenter;

import android.util.Log;

import com.magenta.rx.java.R;
import com.magenta.rx.kotlin.concurrent.RXThreadPool;
import com.magenta.rx.kotlin.event.CalcEvent;
import com.magenta.rx.kotlin.event.CleanEvent;
import com.magenta.rx.kotlin.record.ConcurrentConfig;
import com.magenta.rx.kotlin.record.RowResult;
import com.magenta.rx.kotlin.utils.AlgotithmKt;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class ConcurrentPresenter {

    private final RXThreadPool pool;
    private final ConcurrentConfig concurrentConfig;

    @Inject
    public ConcurrentPresenter(ConcurrentConfig concurrentConfig) {
        this.pool = RXThreadPool.Companion.getInstance();
        this.concurrentConfig = concurrentConfig;
    }

    public void start(boolean multithreading, int progress) {
        if (multithreading && progress > 1) {
            double count = (concurrentConfig.getStart() - concurrentConfig.getEnd()) / concurrentConfig.getStep();
            int coreCount = pool.getCount();
            if ((Math.min(progress, coreCount) >= count) || (progress >= coreCount && progress >= count)) {
                for (int i = concurrentConfig.getStart(); i <= concurrentConfig.getEnd(); i += concurrentConfig.getStep()) {
                    final int finalI = i;
                    pool.put(new Runnable() {
                        public void run() {
                            EventBus.getDefault().postSticky(new CalcEvent(AlgotithmKt.calc(finalI)));
                        }
                    });
                }
            } else {
                // TODO: 11/24/16 your awesome code here
            }
        } else {
            pool.put(new Runnable() {
                public void run() {
                    for (RowResult result : AlgotithmKt.calc(concurrentConfig.getStart(), concurrentConfig.getEnd(), concurrentConfig.getStep())) {
                        EventBus.getDefault().postSticky(new CalcEvent(result));
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
}