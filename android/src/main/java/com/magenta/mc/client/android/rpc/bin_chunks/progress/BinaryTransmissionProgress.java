package com.magenta.mc.client.android.rpc.bin_chunks.progress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BinaryTransmissionProgress {

    private final static BinaryTransmissionProgress instance = new BinaryTransmissionProgress();
    private final Map<String, BinaryTransmissionTaskStatus> uriToStatus = new HashMap<>();
    private final List<BinaryTransmissionTaskStatus> statuses = new ArrayList<>();
    private BinaryTransmissionProgressView view;

    private BinaryTransmissionProgress() {
    }

    public static BinaryTransmissionProgress getInstance() {
        return instance;
    }

    public List<BinaryTransmissionTaskStatus> getStatuses() {
        return statuses;
    }

    public synchronized void transmissionScanned(String transmissionTaskURI, int chunksCount) {
        getStatus(transmissionTaskURI).setTotalChunks(chunksCount);
        updated();
    }

    public synchronized void chunkSent(String transmissionTaskURI) {
        getStatus(transmissionTaskURI).chunkSent();
        updated();
    }

    private BinaryTransmissionTaskStatus getStatus(String transmissionTaskURI) {
        BinaryTransmissionTaskStatus status = uriToStatus.get(transmissionTaskURI);
        if (status == null) {
            status = new BinaryTransmissionTaskStatus();
            status.setUri(transmissionTaskURI);
            uriToStatus.put(transmissionTaskURI, status);
            statuses.add(status);
        }
        return status;
    }

    private void updated() {
        if (view != null && view.isActive()) {
            view.statusesUpdated(statuses);
        }
    }

    public void setBinaryTransmissionProgressView(BinaryTransmissionProgressView view) {
        this.view = view;
    }
}