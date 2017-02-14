package com.magenta.mc.client.android.mc.client.resend;

import com.magenta.mc.client.android.mc.storage.Storable;

/**
 * @author Petr Popov
 *         Created: 08.12.11 15:02
 */
public abstract class Resendable extends Storable {

    private static final long serialVersionUID = 11L;
    private boolean dontResend;
    //true if sent from sendSavedResendable to avoid second sending from timer task
    private boolean sending;

    public abstract boolean send();

    public abstract void setId(String id);
}
