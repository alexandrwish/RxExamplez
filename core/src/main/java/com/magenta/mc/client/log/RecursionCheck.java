package com.magenta.mc.client.log;

/**
 * User: const
 * Date: 16.02.11
 * Time: 14:57
 * <p>
 * A tool for checking recursion state.
 * Run your code possible of recursion using RecursionCheck.execute.
 * RecursionCheck.isRecursion allows to check if we're now in recursion relatively to the
 * execution secured with RecursionCheck.execute.
 */
public class RecursionCheck {
    private ThreadLocal recursiveCheck = new ThreadLocal() {
        public Object get() {
            return super.get();
        }
    };

    public void execute(Runnable action) {
        check();
        action.run();
        uncheck();
    }

    public boolean isRecursion() {
        Boolean recursed = (Boolean) recursiveCheck.get();
        return recursed != null && recursed.equals(Boolean.TRUE);
    }

    public void check() {
        recursiveCheck.set(Boolean.TRUE);
    }

    public void uncheck() {
        recursiveCheck.set(Boolean.FALSE);
    }
}
