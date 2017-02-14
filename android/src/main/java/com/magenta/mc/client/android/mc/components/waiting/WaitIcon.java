package com.magenta.mc.client.android.mc.components.waiting;

import com.magenta.mc.client.android.mc.components.dialogs.DialogCallback;
import com.magenta.mc.client.android.mc.util.FutureRunnable;

/**
 * Created by IntelliJ IDEA.
 * User: const
 * Date: 13.12.11
 * Time: 17:59
 * To change this template use File | Settings | File Templates.
 */
public class WaitIcon {

    protected static WaitIcon current;
    private static WaitIcon impl;
    private DialogCallback callback;
    private FutureRunnable task;

    protected WaitIcon() {
    }

    protected WaitIcon(FutureRunnable task) {
        this.task = task;
    }

    protected WaitIcon(FutureRunnable task, DialogCallback callback) {
        this.task = task;
        this.callback = callback;
    }

    protected WaitIcon(DialogCallback callback) {
        this.callback = callback;
    }

    public static void show(FutureRunnable task) {
        (current = WaitIcon.impl.create(task, null)).show();
    }

    public static void show(FutureRunnable task, DialogCallback callback) {
        (current = WaitIcon.impl.create(task, callback)).show();
    }

    public static void show(FutureRunnable task, DialogCallback callback, boolean inDaemonThread) {
        (current = WaitIcon.impl.create(task, callback)).show(true);
    }

    public static void implement(WaitIcon impl) {
        WaitIcon.impl = impl;
    }

    public static void setShowingPaused(boolean value) {
        WaitIcon.current.setShowingPaused0(value);
    }

    public static WaitIcon getImpl() {
        return impl;
    }

    protected void setShowingPaused0(boolean value) {

    }

    public void show() {
        // todo: implement
    }

    public void show(boolean inDaemonThread) {
        // todo: implement
    }

    public WaitIcon create(FutureRunnable task, DialogCallback callback) {
        return new WaitIcon(task, callback);
    }

    public void setCallback(DialogCallback callback) {
        this.callback = callback;
    }
}
