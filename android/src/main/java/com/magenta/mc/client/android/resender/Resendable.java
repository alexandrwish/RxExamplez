package com.magenta.mc.client.android.resender;

import com.magenta.mc.client.android.storage.Storable;

public abstract class Resendable extends Storable {

    private static final long serialVersionUID = 11L;

    public abstract boolean send();

    public abstract void setId(String id);
}