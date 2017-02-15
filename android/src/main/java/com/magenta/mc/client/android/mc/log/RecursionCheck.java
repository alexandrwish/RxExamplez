package com.magenta.mc.client.android.mc.log;

public class RecursionCheck {

    private ThreadLocal<Boolean> recursiveCheck = new ThreadLocal<>();

    public void execute(Runnable action) {
        check();
        action.run();
        uncheck();
    }

    public boolean isRecursion() {
        Boolean recursed = recursiveCheck.get();
        return recursed != null && recursed.equals(Boolean.TRUE);
    }

    public void check() {
        recursiveCheck.set(Boolean.TRUE);
    }

    public void uncheck() {
        recursiveCheck.set(Boolean.FALSE);
    }
}