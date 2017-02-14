package com.magenta.mc.client.android.mc.bin_chunks.progress;

/**
 * @autor Petr Popov
 * Created 23.08.12 17:47
 */
public class BinaryTransmissionTaskStatus {

    private String uri;
    private int totalchunks;
    private int sentChunks;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getTotalchunks() {
        return totalchunks;
    }

    public void setTotalchunks(int totalchunks) {
        this.totalchunks = totalchunks;
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
