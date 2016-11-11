package com.magenta.mc.client.client.resend;

/**
 * Created with IntelliJ IDEA.
 * User: const
 * Date: 24.08.12
 * Time: 17:39
 * To change this template use File | Settings | File Templates.
 */
public class ManagedResendableMetadata extends ResendableMetadata {
    private ResendableMgmt mgmtAction;

    public ManagedResendableMetadata(String name, boolean common, boolean consecutive, ResendableMgmt mgmtAction) {
        super(name, common, consecutive, true);
        this.mgmtAction = mgmtAction;
    }

    public ManagedResendableMetadata(String name, boolean common, ResendableMgmt mgmtAction) {
        this(name, common, false, mgmtAction);
    }

    public ManagedResendableMetadata(String name, ResendableMgmt mgmtAction) {
        this(name, false, mgmtAction);
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
