package com.magenta.mc.client.android.mc.client.resend;

public class ManagedResendableMetadata extends ResendableMetadata {

    private ResendableMgmt mgmtAction;

    public ManagedResendableMetadata(String name, boolean common, boolean consecutive, ResendableMgmt mgmtAction) {
        super(name, common, consecutive, true);
        this.mgmtAction = mgmtAction;
    }

    public boolean send(Resendable target) {
        super.send(target);
        return mgmtAction.send(target);
    }

    public void sent(ResendableMetadata target, String id, Object[] params) {
        super.sent(target, id, params);
        mgmtAction.sent(target, id, params);
    }
}