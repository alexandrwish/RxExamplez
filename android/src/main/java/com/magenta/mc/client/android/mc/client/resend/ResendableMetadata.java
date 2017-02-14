package com.magenta.mc.client.android.mc.client.resend;

import com.magenta.mc.client.android.mc.components.MCTimerTask;
import com.magenta.mc.client.android.mc.storage.StorableMetadata;

import EDU.oswego.cs.dl.util.concurrent.ReentrantWriterPreferenceReadWriteLock;

/**
 * Created by IntelliJ IDEA.
 * User: const
 * Date: 09.06.12
 * Time: 19:49
 * To change this template use File | Settings | File Templates.
 */
public class ResendableMetadata extends StorableMetadata implements ResendableMgmt {
    public final boolean consecutive;
    public final boolean managed;
    public final boolean reverse;

    public final ReentrantWriterPreferenceReadWriteLock lock = new ReentrantWriterPreferenceReadWriteLock();

    public MCTimerTask errorTimeout;

    public ResendableMetadata(String name, boolean common, boolean consecutive, boolean managed, boolean reverse) {
        super(name, common);
        this.consecutive = consecutive;
        this.managed = managed;
        this.reverse = reverse;
    }

    public ResendableMetadata(String name, boolean common, boolean consecutive, boolean managed) {
        this(name, common, consecutive, managed, false);
    }

    public ResendableMetadata(String name, boolean common, boolean consecutive) {
        this(name, common, consecutive, false);
    }

    public ResendableMetadata(String name, boolean common) {
        this(name, common, false, false);
    }

    public ResendableMetadata(String name) {
        this(name, false, false, false);
    }

    public boolean send(Resendable target) {
        return false;
    }

    public void sent(ResendableMetadata metadata, String id, Object[] params) {

    }
}
