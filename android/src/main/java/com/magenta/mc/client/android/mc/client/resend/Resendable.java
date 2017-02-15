package com.magenta.mc.client.android.mc.client.resend;

import com.magenta.mc.client.android.mc.storage.Storable;

public abstract class Resendable extends Storable {

    private static final long serialVersionUID = 11L;

    public abstract boolean send();

    public abstract void setId(String id);
}