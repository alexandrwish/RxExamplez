package com.magenta.mc.client.android.rpc.xmpp;

import com.magenta.mc.client.android.DistributionApplication;
import com.magenta.mc.client.android.mc.MxSettings;
import com.magenta.mc.client.android.mc.client.Login;
import com.magenta.mc.client.android.mc.settings.Settings;
import com.magenta.mc.client.android.mc.setup.Setup;
import com.magenta.mc.client.android.task.LoginCheckTask;
import com.magenta.mc.client.android.service.ServicesRegistry;
import com.magenta.mc.client.android.util.UserUtils;

import java.io.IOException;
import java.net.Socket;

import okhttp3.Request;

public class Utf8IOStream extends com.magenta.mc.client.android.mc.io.Utf8IOStream {

    public Utf8IOStream(Socket socket) throws IOException {
        super(socket);
    }

    public void send(StringBuffer data) throws IOException {
        if (data.toString().equalsIgnoreCase("</stream:stream>") || data.toString().contains("urn:xmpp:ping") || data.toString().contains("</presence>") || data.toString().contains("processPerformerLogging")) {
            super.send(data);
            return;
        }
        if (Boolean.valueOf((String) Setup.get().getSettings().get(MxSettings.ENABLE_API)) && !DistributionApplication.getInstance().isLoginPress()) {
            String driver = UserUtils.cutComponentName(Settings.get().getUserId());
            String result = XMPPStream2.getThreadSafeClient().newCall(new Request.Builder().url(LoginCheckTask.generateLoginURL(driver)).get().build()).execute().body().string();
            if (result.isEmpty()) {
                super.send(data);
            } else {
                Login.getInstance().logout(true);
                ServicesRegistry.getWorkflowService().logout();
            }
        } else {
            super.send(data);
        }
    }
}