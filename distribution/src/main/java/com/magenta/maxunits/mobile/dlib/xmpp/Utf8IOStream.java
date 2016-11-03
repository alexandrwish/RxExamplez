package com.magenta.maxunits.mobile.dlib.xmpp;

import com.magenta.maxunits.mobile.MxApplication;
import com.magenta.maxunits.mobile.dlib.receiver.LoginCheckReceiver;
import com.magenta.maxunits.mobile.mc.MxAndroidUtil;
import com.magenta.maxunits.mobile.mc.MxSettings;
import com.magenta.maxunits.mobile.service.ServicesRegistry;
import com.magenta.maxunits.mobile.utils.UserUtils;
import com.magenta.mc.client.client.Login;
import com.magenta.mc.client.settings.Settings;
import com.magenta.mc.client.setup.Setup;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;
import java.net.Socket;

public class Utf8IOStream extends com.magenta.mc.client.io.Utf8IOStream {

    public Utf8IOStream(Socket socket) throws IOException {
        super(socket);
    }

    public void send(StringBuffer data) throws IOException {
        if (data.toString().equalsIgnoreCase("</stream:stream>") || data.toString().contains("urn:xmpp:ping") || data.toString().contains("</presence>") || data.toString().contains("processPerformerLogging")) {
            super.send(data);
            return;
        }
        if (Boolean.valueOf((String) Setup.get().getSettings().get(MxSettings.ENABLE_API)) && !MxApplication.getInstance().isLoginPress()) {
            String driver = UserUtils.cutComponentName(Settings.get().getUserId());
            HttpResponse response = XMPPStream2.getThreadSafeClient().execute(new HttpGet(LoginCheckReceiver.generateLoginURL(driver)));
            String result = MxAndroidUtil.readResponse(response.getEntity().getContent());
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