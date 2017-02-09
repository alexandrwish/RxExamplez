package com.magenta.maxunits.mobile.dlib.rpc.operations;

import com.magenta.maxunits.mobile.dlib.rpc.RPCOut;
import com.magenta.mc.client.components.waiting.LongOperationWithTimeout;

public class JobsRefreshing extends LongOperationWithTimeout {

    private static JobsRefreshing instance;

    public JobsRefreshing(Runnable operation, long timeout) {
        super(operation, timeout);
    }

    public static JobsRefreshing getInstance() {
        if (instance == null) {
            instance = new JobsRefreshing(new Runnable() {
                public void run() {
                    RPCOut.reloadJobs();
                }
            }, 10000L);
        }
        return instance;
    }

    public static void refresh() {
        getInstance().run();
    }

    public static void refreshDone() {
        getInstance().done();
    }
}