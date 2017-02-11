package com.magenta.mc.client.android.rpc.operations;

import com.magenta.mc.client.android.service.ServicesRegistry;
import com.magenta.mc.client.client.Login;
import com.magenta.mc.client.client.XMPPClient;

public class LogoutLock {

    private static final LogoutLock INSTANCE = new LogoutLock();

    private final Object lock = new Object();

    private LogoutLock() {
    }

    public static LogoutLock getInstance() {
        return INSTANCE;
    }

    public void logout() {
        synchronized (lock) {
            Login.getInstance().logout();
            while (XMPPClient.getInstance().isLoggedIn()) {
                try {
                    lock.wait(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
        System.out.println("clearing datacontroller");
        ServicesRegistry.getDataController().clear();
        System.out.println("clearing datacontroller done");
    }
}