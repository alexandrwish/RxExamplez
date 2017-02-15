package com.magenta.mc.client.android.rpc.bin_chunks.progress;

public class BinaryTransmissionTaskStatus {

    private String uri;
    private int totalChunks;
    private int sentChunks;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getTotalChunks() {
        return totalChunks;
    }

    public void setTotalChunks(int totalChunks) {
        this.totalChunks = totalChunks;
    }

    public int getSentChunks() {
        return sentChunks;
    }

    public void setSentChunks(int sentChunks) {
        this.sentChunks = sentChunks;
    }

    void chunkSent() {
        sentChunks++;
    }
}