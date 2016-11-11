package com.magenta.mc.client.bin_chunks.progress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @autor Petr Popov
 * Created 23.08.12 17:38
 */
public class BinaryTransmissionProgress {

    private static BinaryTransmissionProgress instance = new BinaryTransmissionProgress();
    private List statuses = new ArrayList();
    private BinaryTransmissionProgressView view;
    private HashMap uriToStatus = new HashMap();

    private BinaryTransmissionProgress() {
    }

    public static BinaryTransmissionProgress getInstance() {
        return instance;
    }

    public List getStatuses() {
        return statuses;
    }

    public synchronized void transmissionScanned(String transmissionTaskURI, int chunksCount) {
        getStatus(transmissionTaskURI).setTotalchunks(chunksCount);
        updated();
    }

    public synchronized void chunkSent(String transmissionTaskURI) {
        getStatus(transmissionTaskURI).chunkSent();
        updated();
    }

    private BinaryTransmissionTaskStatus getStatus(String transmissionTaskURI) {
        BinaryTransmissionTaskStatus status = (BinaryTransmissionTaskStatus) uriToStatus.get(transmissionTaskURI);
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
