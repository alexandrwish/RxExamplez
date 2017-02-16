package com.magenta.mc.client.android.service;

import android.app.IntentService;
import android.content.Intent;

import com.magenta.mc.client.android.common.Constants;

public class SenderService extends IntentService {

    public SenderService() {
        super(Constants.SENDER_SERVICE_NAME);
    }

    protected void onHandleIntent(Intent intent) {
        // TODO: 2/16/17 сделать отправку через сервис
    }
}