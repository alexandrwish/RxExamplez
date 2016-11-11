package com.magenta.mc.client.client.resend;

/**
 * Created with IntelliJ IDEA.
 * User: const
 * Date: 24.08.12
 * Time: 17:35
 * To change this template use File | Settings | File Templates.
 */
public interface ResendableMgmt {
    boolean send(Resendable target);

    void sent(ResendableMetadata metadata, String id, Object[] params);
}
