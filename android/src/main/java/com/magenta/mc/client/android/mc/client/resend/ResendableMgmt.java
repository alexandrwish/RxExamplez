package com.magenta.mc.client.android.mc.client.resend;

public interface ResendableMgmt {

    boolean send(Resendable target);

    void sent(ResendableMetadata metadata, String id, Object[] params);
}