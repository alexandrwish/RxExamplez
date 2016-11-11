package com.magenta.mc.client.bin_chunks.progress;

import java.util.List;

/**
 * @autor Petr Popov
 * Created 24.08.12 14:03
 */
public interface BinaryTransmissionProgressView {

    void statusesUpdated(List transmissionsStatuses);

    boolean isActive();

}
