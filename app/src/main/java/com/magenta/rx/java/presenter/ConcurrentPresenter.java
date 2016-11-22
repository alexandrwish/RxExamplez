package com.magenta.rx.java.presenter;

import android.util.Log;

import com.magenta.rx.java.R;
import com.magenta.rx.kotlin.concurrent.RXThreadPool;
import com.magenta.rx.kotlin.record.ConcurrentConfig;

import javax.inject.Inject;

public class ConcurrentPresenter {

    private final ConcurrentConfig concurrentConfig;

    @Inject
    public ConcurrentPresenter(ConcurrentConfig concurrentConfig) {
        this.concurrentConfig = concurrentConfig;
    }

    public void start(boolean multithreading, int progress) {
        if (multithreading) {
            for (int i = 0; i < progress; i++) {
                RXThreadPool.Companion.getInstance().put(new Runnable() {
                    public void run() {
                        // TODO: 11/22/16 calculate
                    }
                });
            }
        } else {
            RXThreadPool.Companion.getInstance().put(new Runnable() {
                public void run() {
                    // TODO: 11/22/16 calculate
                }
            });
        }
    }

    public void change(int id, CharSequence charSequence) {
        if (charSequence != null && charSequence.length() > 0) {
            try {
                switch (id) {
                    case R.id.xn: {
                        concurrentConfig.setXn(Integer.valueOf(String.valueOf(charSequence)));
                        break;
                    }
                    case R.id.xk: {
                        concurrentConfig.setXk(Integer.valueOf(String.valueOf(charSequence)));
                        break;
                    }
                    case R.id.dx: {
                        concurrentConfig.setDx(Integer.valueOf(String.valueOf(charSequence)));
                        break;
                    }
                    case R.id.eps: {
                        concurrentConfig.setEps(Integer.valueOf(String.valueOf(charSequence)));
                        break;
                    }
                }
            } catch (Exception e) {
                Log.e(getClass().getName(), e.getMessage(), e);
            }
        }
    }
}