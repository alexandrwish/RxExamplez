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

    private final ConcurrentConfig concurrentConfig;

    @Inject
    public ConcurrentPresenter(ConcurrentConfig concurrentConfig) {
        this.concurrentConfig = concurrentConfig;
    }

    public void start(boolean multithreading, int progress) {
        if (multithreading) {
            double x = concurrentConfig.getXn();
            while (x < concurrentConfig.getXk()) {
                for (int i = 0; i < progress; i++) {
                    final double finalX = x;
                    RXThreadPool.Companion.getInstance().put(new Runnable() {
                        public void run() {
                            EventBus.getDefault().postSticky(AlgotithmKt.singleCalc(concurrentConfig.getEps(), finalX));
                        }
                    });
                    if (x >= concurrentConfig.getXk()) {
                        break;
                    } else {
                        x += concurrentConfig.getDx();
                    }
                }
            }
        } else {
            RXThreadPool.Companion.getInstance().put(new Runnable() {
                public void run() {
                    for (RowResult result : AlgotithmKt.calc(concurrentConfig.getXn(), concurrentConfig.getXk(), concurrentConfig.getDx(), concurrentConfig.getEps())) {
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
                    case R.id.xn: {
                        concurrentConfig.setXn(Double.valueOf(String.valueOf(charSequence)));
                        break;
                    }
                    case R.id.xk: {
                        concurrentConfig.setXk(Double.valueOf(String.valueOf(charSequence)));
                        break;
                    }
                    case R.id.dx: {
                        concurrentConfig.setDx(Double.valueOf(String.valueOf(charSequence)));
                        break;
                    }
                    case R.id.eps: {
                        concurrentConfig.setEps(Double.valueOf(String.valueOf(charSequence)));
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