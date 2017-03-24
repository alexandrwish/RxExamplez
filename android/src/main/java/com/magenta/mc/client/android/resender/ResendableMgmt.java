package com.magenta.mc.client.android.resender;

public interface ResendableMgmt {

    boolean send(Resendable target);

    void sent(ResendableMetadata metadata, String id, Object[] params);
}