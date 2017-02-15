package com.magenta.mc.client.android.rpc.bin_chunks.progress;

import java.util.List;

public interface BinaryTransmissionProgressView {

    void statusesUpdated(List<BinaryTransmissionTaskStatus> transmissionsStatuses);

    boolean isActive();
}